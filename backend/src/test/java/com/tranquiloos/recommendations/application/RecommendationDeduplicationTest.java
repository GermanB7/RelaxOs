package com.tranquiloos.recommendations.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.expenses.api.ScenarioExpenseResponse;
import com.tranquiloos.expenses.application.ExpenseService;
import com.tranquiloos.expenses.domain.ExpenseFrequency;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationStatus;
import com.tranquiloos.recommendations.infrastructure.RecommendationEntity;
import com.tranquiloos.recommendations.infrastructure.RecommendationJpaRepository;
import com.tranquiloos.scenarios.application.ScenarioService;
import com.tranquiloos.scenarios.domain.ScenarioStatus;
import com.tranquiloos.scenarios.infrastructure.ScenarioEntity;
import com.tranquiloos.scoring.domain.ConfidenceLevel;
import com.tranquiloos.scoring.domain.ScoreStatus;
import com.tranquiloos.scoring.infrastructure.RiskFactorRepository;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotEntity;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotRepository;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class RecommendationDeduplicationTest {

	@Test
	void recalculationDoesNotDuplicateExistingOpenRecommendationAndUpdatesIt() {
		CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
		ScenarioService scenarioService = mock(ScenarioService.class);
		ExpenseService expenseService = mock(ExpenseService.class);
		ScoreSnapshotRepository scoreSnapshotRepository = mock(ScoreSnapshotRepository.class);
		RiskFactorRepository riskFactorRepository = mock(RiskFactorRepository.class);
		RecommendationJpaRepository recommendationRepository = mock(RecommendationJpaRepository.class);
		RecommendationEngineService service = new RecommendationEngineService(
				currentUserProvider,
				scenarioService,
				expenseService,
				scoreSnapshotRepository,
				riskFactorRepository,
				recommendationRepository,
				new RecommendationMapper(),
				new ObjectMapper());

		ScenarioEntity scenario = scenario();
		ScoreSnapshotEntity snapshot = snapshot();
		RecommendationEntity existing = existingOpenRecommendation();
		when(currentUserProvider.currentUserId()).thenReturn(1L);
		when(scenarioService.findCurrentUserScenario(10L)).thenReturn(scenario);
		when(scoreSnapshotRepository.findFirstByScenarioIdOrderByCreatedAtDesc(10L)).thenReturn(Optional.of(snapshot));
		when(expenseService.listScenarioExpenses(10L)).thenReturn(expenses());
		when(riskFactorRepository.findByScoreSnapshotIdOrderByIdAsc(30L)).thenReturn(List.of());
		when(recommendationRepository.findByUserIdAndScenarioIdAndSourceRuleKeyAndStatus(
				any(Long.class),
				any(Long.class),
				anyString(),
				any(RecommendationStatus.class))).thenAnswer(invocation -> {
					String ruleKey = invocation.getArgument(2);
					return "NEGATIVE_MARGIN_RULE".equals(ruleKey) ? Optional.of(existing) : Optional.empty();
				});
		when(recommendationRepository.save(any(RecommendationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

		var response = service.recalculate(10L);

		assertThat(response.generatedCount()).isGreaterThanOrEqualTo(2);
		assertThat(existing.getSeverity()).isEqualTo(RecommendationSeverity.CRITICAL);
		assertThat(existing.getPriority()).isEqualTo(1);
		assertThat(existing.getStatus()).isEqualTo(RecommendationStatus.OPEN);
		verify(recommendationRepository).save(existing);
	}

	private ScenarioEntity scenario() {
		ScenarioEntity entity = new ScenarioEntity();
		ReflectionTestUtils.setField(entity, "id", 10L);
		ReflectionTestUtils.setField(entity, "createdAt", Instant.now());
		ReflectionTestUtils.setField(entity, "updatedAt", Instant.now());
		entity.setUserId(1L);
		entity.setName("Test scenario");
		entity.setMonthlyIncome(BigDecimal.valueOf(3000));
		entity.setEmergencyFundCurrent(BigDecimal.ZERO);
		entity.setStatus(ScenarioStatus.DRAFT);
		return entity;
	}

	private ScoreSnapshotEntity snapshot() {
		ScoreSnapshotEntity entity = new ScoreSnapshotEntity();
		ReflectionTestUtils.setField(entity, "id", 30L);
		ReflectionTestUtils.setField(entity, "createdAt", Instant.now());
		entity.setScenarioId(10L);
		entity.setScore(35);
		entity.setStatus(ScoreStatus.NOT_RECOMMENDED);
		entity.setConfidenceLevel(ConfidenceLevel.MEDIUM);
		entity.setSummary("Test summary");
		return entity;
	}

	private RecommendationEntity existingOpenRecommendation() {
		RecommendationEntity entity = new RecommendationEntity();
		ReflectionTestUtils.setField(entity, "id", 50L);
		ReflectionTestUtils.setField(entity, "createdAt", Instant.now());
		ReflectionTestUtils.setField(entity, "updatedAt", Instant.now());
		entity.setUserId(1L);
		entity.setScenarioId(10L);
		entity.setSourceRuleKey("NEGATIVE_MARGIN_RULE");
		entity.setStatus(RecommendationStatus.OPEN);
		return entity;
	}

	private List<ScenarioExpenseResponse> expenses() {
		return List.of(
				expense(1L, "rent", BigDecimal.valueOf(2600), true),
				expense(2L, "groceries", BigDecimal.valueOf(700), true),
				expense(3L, "utilities", BigDecimal.valueOf(300), true),
				expense(4L, "internet", BigDecimal.valueOf(120), true));
	}

	private ScenarioExpenseResponse expense(Long id, String categoryCode, BigDecimal monthlyEquivalent, boolean essential) {
		return new ScenarioExpenseResponse(
				id,
				10L,
				id,
				categoryCode,
				categoryCode,
				categoryCode,
				monthlyEquivalent,
				ExpenseFrequency.MONTHLY,
				essential,
				monthlyEquivalent);
	}
}
