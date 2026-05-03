package com.tranquiloos.modes.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.modes.api.ActivateModeRequest;
import com.tranquiloos.modes.api.EndModeRequest;
import com.tranquiloos.modes.domain.AlertPolicy;
import com.tranquiloos.modes.domain.IntensityLevel;
import com.tranquiloos.modes.domain.ModeActivationStatus;
import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.modes.domain.PurchasePolicy;
import com.tranquiloos.modes.domain.RoutinePolicy;
import com.tranquiloos.modes.domain.SpendingPolicy;
import com.tranquiloos.modes.infrastructure.AdaptiveModeEntity;
import com.tranquiloos.modes.infrastructure.AdaptiveModeJpaRepository;
import com.tranquiloos.modes.infrastructure.ModeActivationEntity;
import com.tranquiloos.modes.infrastructure.ModeActivationJpaRepository;
import com.tranquiloos.recommendations.infrastructure.DecisionEventEntity;
import com.tranquiloos.recommendations.infrastructure.DecisionEventJpaRepository;
import com.tranquiloos.scenarios.application.ScenarioService;
import com.tranquiloos.shared.error.ResourceNotFoundException;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

class ModeActivationServiceTest {

	@Test
	void activateCreatesActiveActivationAndDecisionEvent() {
		TestHarness harness = harness();
		when(harness.modeRepository.findByCodeAndActiveTrue(ModeCode.WAR_MODE)).thenReturn(Optional.of(mode(1L, ModeCode.WAR_MODE)));
		when(harness.activationRepository.findFirstByUserIdAndStatusOrderByActivatedAtDesc(1L, ModeActivationStatus.ACTIVE)).thenReturn(Optional.empty());
		when(harness.activationRepository.save(any(ModeActivationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

		var response = harness.service.activate(new ActivateModeRequest(ModeCode.WAR_MODE, null, "Save", 30, IntensityLevel.HIGH, "test"));

		assertThat(response.status()).isEqualTo(ModeActivationStatus.ACTIVE);
		assertThat(response.modeCode()).isEqualTo(ModeCode.WAR_MODE);
		ArgumentCaptor<DecisionEventEntity> eventCaptor = ArgumentCaptor.forClass(DecisionEventEntity.class);
		verify(harness.decisionRepository).save(eventCaptor.capture());
		assertThat(eventCaptor.getValue().getDecisionType()).isEqualTo("MODE_ACTIVATED");
	}

	@Test
	void activatingNewModeEndsPreviousActiveMode() {
		TestHarness harness = harness();
		ModeActivationEntity previous = activation(10L, 1L, ModeActivationStatus.ACTIVE);
		when(harness.modeRepository.findByCodeAndActiveTrue(ModeCode.STABLE_MODE)).thenReturn(Optional.of(mode(2L, ModeCode.STABLE_MODE)));
		when(harness.modeRepository.findById(1L)).thenReturn(Optional.of(mode(1L, ModeCode.WAR_MODE)));
		when(harness.activationRepository.findFirstByUserIdAndStatusOrderByActivatedAtDesc(1L, ModeActivationStatus.ACTIVE)).thenReturn(Optional.of(previous));
		when(harness.activationRepository.save(any(ModeActivationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

		harness.service.activate(new ActivateModeRequest(ModeCode.STABLE_MODE, null, "Stabilize", 14, null, null));

		assertThat(previous.getStatus()).isEqualTo(ModeActivationStatus.ENDED);
		assertThat(previous.getEndedAt()).isNotNull();
	}

	@Test
	void endActiveModeSetsEndedAndCreatesDecisionEvent() {
		TestHarness harness = harness();
		ModeActivationEntity activation = activation(10L, 1L, ModeActivationStatus.ACTIVE);
		when(harness.activationRepository.findFirstByUserIdAndStatusOrderByActivatedAtDesc(1L, ModeActivationStatus.ACTIVE)).thenReturn(Optional.of(activation));
		when(harness.modeRepository.findById(1L)).thenReturn(Optional.of(mode(1L, ModeCode.WAR_MODE)));
		when(harness.activationRepository.save(any(ModeActivationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

		harness.service.endActive(new EndModeRequest("Done"));

		assertThat(activation.getStatus()).isEqualTo(ModeActivationStatus.ENDED);
		assertThat(activation.getEndedAt()).isNotNull();
		ArgumentCaptor<DecisionEventEntity> eventCaptor = ArgumentCaptor.forClass(DecisionEventEntity.class);
		verify(harness.decisionRepository).save(eventCaptor.capture());
		assertThat(eventCaptor.getValue().getDecisionType()).isEqualTo("MODE_ENDED");
	}

	@Test
	void cannotActivateUnknownMode() {
		TestHarness harness = harness();
		when(harness.modeRepository.findByCodeAndActiveTrue(ModeCode.RESET_MODE)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> harness.service.activate(new ActivateModeRequest(ModeCode.RESET_MODE, null, null, null, null, null)))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	private TestHarness harness() {
		CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
		AdaptiveModeJpaRepository modeRepository = mock(AdaptiveModeJpaRepository.class);
		ModeActivationJpaRepository activationRepository = mock(ModeActivationJpaRepository.class);
		DecisionEventJpaRepository decisionRepository = mock(DecisionEventJpaRepository.class);
		ScenarioService scenarioService = mock(ScenarioService.class);
		ActiveModeProvider activeModeProvider = mock(ActiveModeProvider.class);
		when(currentUserProvider.currentUserId()).thenReturn(1L);
		ModeActivationService service = new ModeActivationService(
				currentUserProvider,
				modeRepository,
				activationRepository,
				decisionRepository,
				scenarioService,
				activeModeProvider,
				new ObjectMapper());
		return new TestHarness(service, modeRepository, activationRepository, decisionRepository);
	}

	private AdaptiveModeEntity mode(Long id, ModeCode code) {
		AdaptiveModeEntity mode = new AdaptiveModeEntity();
		ReflectionTestUtils.setField(mode, "id", id);
		ReflectionTestUtils.setField(mode, "code", code);
		ReflectionTestUtils.setField(mode, "name", code.name());
		ReflectionTestUtils.setField(mode, "recommendedMaxDays", 30);
		ReflectionTestUtils.setField(mode, "intensityLevel", IntensityLevel.HIGH);
		ReflectionTestUtils.setField(mode, "spendingPolicy", SpendingPolicy.STRICT);
		ReflectionTestUtils.setField(mode, "alertPolicy", AlertPolicy.STRICT);
		ReflectionTestUtils.setField(mode, "purchasePolicy", PurchasePolicy.FREEZE_NON_ESSENTIAL);
		ReflectionTestUtils.setField(mode, "routinePolicy", RoutinePolicy.STRICT);
		ReflectionTestUtils.setField(mode, "active", true);
		ReflectionTestUtils.setField(mode, "sortOrder", 10);
		return mode;
	}

	private ModeActivationEntity activation(Long id, Long modeId, ModeActivationStatus status) {
		ModeActivationEntity activation = new ModeActivationEntity();
		ReflectionTestUtils.setField(activation, "id", id);
		activation.setUserId(1L);
		activation.setModeId(modeId);
		activation.setStatus(status);
		activation.setActivatedAt(Instant.now());
		return activation;
	}

	private record TestHarness(
			ModeActivationService service,
			AdaptiveModeJpaRepository modeRepository,
			ModeActivationJpaRepository activationRepository,
			DecisionEventJpaRepository decisionRepository) {
	}
}
