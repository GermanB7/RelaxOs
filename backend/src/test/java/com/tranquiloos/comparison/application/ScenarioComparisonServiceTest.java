package com.tranquiloos.comparison.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.comparison.api.CompareScenariosRequest;
import com.tranquiloos.comparison.api.ScenarioComparisonResponse;
import com.tranquiloos.decisions.application.DecisionEventService;
import com.tranquiloos.expenses.api.ScenarioExpenseResponse;
import com.tranquiloos.expenses.application.ExpenseService;
import com.tranquiloos.expenses.domain.ExpenseFrequency;
import com.tranquiloos.recommendations.infrastructure.RecommendationJpaRepository;
import com.tranquiloos.scenarios.application.ScenarioService;
import com.tranquiloos.scenarios.infrastructure.ScenarioEntity;
import com.tranquiloos.scoring.domain.ConfidenceLevel;
import com.tranquiloos.scoring.domain.RiskSeverity;
import com.tranquiloos.scoring.domain.ScoreStatus;
import com.tranquiloos.scoring.infrastructure.RiskFactorEntity;
import com.tranquiloos.scoring.infrastructure.RiskFactorRepository;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotEntity;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotRepository;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ScenarioComparisonServiceTest {

	@Mock private CurrentUserProvider currentUserProvider;
	@Mock private ScenarioService scenarioService;
	@Mock private ExpenseService expenseService;
	@Mock private ScoreSnapshotRepository scoreSnapshotRepository;
	@Mock private RiskFactorRepository riskFactorRepository;
	@Mock private RecommendationJpaRepository recommendationRepository;
	@Mock private DecisionEventService decisionEventService;

	@Test
	void doesNotAutomaticallyPickHigherScoreWhenCriticalRiskIsWorse() {
		when(currentUserProvider.currentUserId()).thenReturn(1L);
		when(scenarioService.findCurrentUserScenario(1L)).thenReturn(scenario(1L, "High score fragile", "4000000"));
		when(scenarioService.findCurrentUserScenario(2L)).thenReturn(scenario(2L, "Stable option", "4000000"));
		when(expenseService.listScenarioExpenses(1L)).thenReturn(List.of(expense("rent", "2500000", true)));
		when(expenseService.listScenarioExpenses(2L)).thenReturn(List.of(expense("rent", "1500000", true)));
		ScoreSnapshotEntity highScore = snapshot(1L, 85);
		ScoreSnapshotEntity stableScore = snapshot(2L, 72);
		when(scoreSnapshotRepository.findFirstByScenarioIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(highScore));
		when(scoreSnapshotRepository.findFirstByScenarioIdOrderByCreatedAtDesc(2L)).thenReturn(Optional.of(stableScore));
		when(riskFactorRepository.findByScoreSnapshotIdOrderByIdAsc(highScore.getId())).thenReturn(List.of(risk(RiskSeverity.CRITICAL)));
		when(riskFactorRepository.findByScoreSnapshotIdOrderByIdAsc(stableScore.getId())).thenReturn(List.of(risk(RiskSeverity.LOW)));
		when(recommendationRepository.findByUserIdAndScenarioIdAndStatusOrderByPriorityAscSeverityDescCreatedAtDesc(any(), any(), any()))
				.thenReturn(List.of());
		ScenarioComparisonService service = new ScenarioComparisonService(
				currentUserProvider,
				scenarioService,
				expenseService,
				scoreSnapshotRepository,
				riskFactorRepository,
				recommendationRepository,
				decisionEventService,
				new ObjectMapper());

		ScenarioComparisonResponse response = service.compare(new CompareScenariosRequest(List.of(1L, 2L)));

		assertThat(response.recommendedScenarioId()).isEqualTo(2L);
	}

	private ScenarioEntity scenario(Long id, String name, String income) {
		ScenarioEntity scenario = new ScenarioEntity();
		ReflectionTestUtils.setField(scenario, "id", id);
		scenario.setUserId(1L);
		scenario.setName(name);
		scenario.setMonthlyIncome(new BigDecimal(income));
		scenario.setEmergencyFundCurrent(new BigDecimal("5000000"));
		return scenario;
	}

	private ScenarioExpenseResponse expense(String categoryCode, String amount, boolean essential) {
		return new ScenarioExpenseResponse(1L, 1L, 1L, categoryCode, categoryCode, categoryCode, new BigDecimal(amount), ExpenseFrequency.MONTHLY, essential, new BigDecimal(amount));
	}

	private ScoreSnapshotEntity snapshot(Long scenarioId, int score) {
		ScoreSnapshotEntity snapshot = new ScoreSnapshotEntity();
		ReflectionTestUtils.setField(snapshot, "id", scenarioId * 10);
		ReflectionTestUtils.setField(snapshot, "createdAt", java.time.Instant.now());
		snapshot.setScenarioId(scenarioId);
		snapshot.setScore(score);
		snapshot.setStatus(ScoreStatus.STABLE_BUT_SENSITIVE);
		snapshot.setConfidenceLevel(ConfidenceLevel.HIGH);
		return snapshot;
	}

	private RiskFactorEntity risk(RiskSeverity severity) {
		RiskFactorEntity risk = new RiskFactorEntity();
		risk.setRiskKey(severity.name());
		risk.setSeverity(severity);
		risk.setTitle(severity.name());
		return risk;
	}
}
