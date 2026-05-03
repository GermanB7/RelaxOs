package com.tranquiloos.recommendations.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.recommendations.api.RecommendationActionRequest;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationStatus;
import com.tranquiloos.recommendations.domain.RecommendationType;
import com.tranquiloos.recommendations.infrastructure.DecisionEventEntity;
import com.tranquiloos.recommendations.infrastructure.DecisionEventJpaRepository;
import com.tranquiloos.recommendations.infrastructure.RecommendationEntity;
import com.tranquiloos.recommendations.infrastructure.RecommendationJpaRepository;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotRepository;
import com.tranquiloos.shared.error.ResourceNotFoundException;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

class RecommendationActionServiceTest {

	@Test
	void acceptChangesStatusAndCreatesDecisionEvent() {
		TestHarness harness = harness();

		var response = harness.service.accept(20L, new RecommendationActionRequest("Makes sense"));

		assertThat(response.status()).isEqualTo(RecommendationStatus.ACCEPTED);
		ArgumentCaptor<DecisionEventEntity> eventCaptor = ArgumentCaptor.forClass(DecisionEventEntity.class);
		verify(harness.decisionEventRepository).save(eventCaptor.capture());
		assertThat(eventCaptor.getValue().getDecisionType()).isEqualTo("RECOMMENDATION_ACCEPTED");
		assertThat(eventCaptor.getValue().getChosenOption()).isEqualTo("ACCEPTED");
		assertThat(eventCaptor.getValue().getReason()).isEqualTo("Makes sense");
	}

	@Test
	void postponeChangesStatusAndCreatesDecisionEvent() {
		TestHarness harness = harness();

		var response = harness.service.postpone(20L, new RecommendationActionRequest("Later"));

		assertThat(response.status()).isEqualTo(RecommendationStatus.POSTPONED);
		ArgumentCaptor<DecisionEventEntity> eventCaptor = ArgumentCaptor.forClass(DecisionEventEntity.class);
		verify(harness.decisionEventRepository).save(eventCaptor.capture());
		assertThat(eventCaptor.getValue().getDecisionType()).isEqualTo("RECOMMENDATION_POSTPONED");
		assertThat(eventCaptor.getValue().getChosenOption()).isEqualTo("POSTPONED");
	}

	@Test
	void dismissChangesStatusAndCreatesDecisionEvent() {
		TestHarness harness = harness();

		var response = harness.service.dismiss(20L, new RecommendationActionRequest("Not useful"));

		assertThat(response.status()).isEqualTo(RecommendationStatus.DISMISSED);
		ArgumentCaptor<DecisionEventEntity> eventCaptor = ArgumentCaptor.forClass(DecisionEventEntity.class);
		verify(harness.decisionEventRepository).save(eventCaptor.capture());
		assertThat(eventCaptor.getValue().getDecisionType()).isEqualTo("RECOMMENDATION_DISMISSED");
		assertThat(eventCaptor.getValue().getChosenOption()).isEqualTo("DISMISSED");
	}

	@Test
	void recommendationActionChecksOwnership() {
		CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
		RecommendationJpaRepository recommendationRepository = mock(RecommendationJpaRepository.class);
		when(currentUserProvider.currentUserId()).thenReturn(2L);
		when(recommendationRepository.findByIdAndUserId(20L, 2L)).thenReturn(Optional.empty());
		RecommendationActionService service = new RecommendationActionService(
				currentUserProvider,
				recommendationRepository,
				mock(DecisionEventJpaRepository.class),
				mock(ScoreSnapshotRepository.class),
				new RecommendationMapper(),
				new ObjectMapper());

		org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.accept(20L, new RecommendationActionRequest("Nope")))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	private TestHarness harness() {
		CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
		RecommendationJpaRepository recommendationRepository = mock(RecommendationJpaRepository.class);
		DecisionEventJpaRepository decisionEventRepository = mock(DecisionEventJpaRepository.class);
		ScoreSnapshotRepository scoreSnapshotRepository = mock(ScoreSnapshotRepository.class);
		RecommendationEntity recommendation = recommendation();
		when(currentUserProvider.currentUserId()).thenReturn(1L);
		when(recommendationRepository.findByIdAndUserId(20L, 1L)).thenReturn(Optional.of(recommendation));
		when(recommendationRepository.save(any(RecommendationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
		RecommendationActionService service = new RecommendationActionService(
				currentUserProvider,
				recommendationRepository,
				decisionEventRepository,
				scoreSnapshotRepository,
				new RecommendationMapper(),
				new ObjectMapper());
		return new TestHarness(service, decisionEventRepository);
	}

	private RecommendationEntity recommendation() {
		RecommendationEntity entity = new RecommendationEntity();
		ReflectionTestUtils.setField(entity, "id", 20L);
		ReflectionTestUtils.setField(entity, "createdAt", Instant.now());
		ReflectionTestUtils.setField(entity, "updatedAt", Instant.now());
		entity.setUserId(1L);
		entity.setScenarioId(10L);
		entity.setScoreSnapshotId(null);
		entity.setType(RecommendationType.MONTHLY_MARGIN);
		entity.setSeverity(RecommendationSeverity.CRITICAL);
		entity.setPriority(1);
		entity.setTitle("Monthly margin is negative");
		entity.setMessage("Message");
		entity.setActionLabel("Adjust scenario expenses");
		entity.setActionType("OPEN_SCENARIO");
		entity.setSourceRuleKey("NEGATIVE_MARGIN_RULE");
		entity.setStatus(RecommendationStatus.OPEN);
		return entity;
	}

	private record TestHarness(
			RecommendationActionService service,
			DecisionEventJpaRepository decisionEventRepository) {
	}
}
