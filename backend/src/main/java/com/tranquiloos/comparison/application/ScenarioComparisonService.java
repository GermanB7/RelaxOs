package com.tranquiloos.comparison.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.comparison.api.CompareScenariosRequest;
import com.tranquiloos.comparison.api.ScenarioComparisonItemResponse;
import com.tranquiloos.comparison.api.ScenarioComparisonResponse;
import com.tranquiloos.comparison.api.SelectScenarioRequest;
import com.tranquiloos.decisions.application.DecisionEventService;
import com.tranquiloos.decisions.domain.DecisionType;
import com.tranquiloos.expenses.api.ScenarioExpenseResponse;
import com.tranquiloos.expenses.application.ExpenseService;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationStatus;
import com.tranquiloos.recommendations.infrastructure.RecommendationJpaRepository;
import com.tranquiloos.scenarios.application.ScenarioService;
import com.tranquiloos.scenarios.infrastructure.ScenarioEntity;
import com.tranquiloos.scoring.domain.RiskSeverity;
import com.tranquiloos.scoring.infrastructure.RiskFactorEntity;
import com.tranquiloos.scoring.infrastructure.RiskFactorRepository;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotEntity;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotRepository;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScenarioComparisonService {

	private static final Duration STALE_SCORE_AFTER = Duration.ofDays(7);

	private final CurrentUserProvider currentUserProvider;
	private final ScenarioService scenarioService;
	private final ExpenseService expenseService;
	private final ScoreSnapshotRepository scoreSnapshotRepository;
	private final RiskFactorRepository riskFactorRepository;
	private final RecommendationJpaRepository recommendationRepository;
	private final DecisionEventService decisionEventService;
	private final ObjectMapper objectMapper;

	public ScenarioComparisonService(
			CurrentUserProvider currentUserProvider,
			ScenarioService scenarioService,
			ExpenseService expenseService,
			ScoreSnapshotRepository scoreSnapshotRepository,
			RiskFactorRepository riskFactorRepository,
			RecommendationJpaRepository recommendationRepository,
			DecisionEventService decisionEventService,
			ObjectMapper objectMapper) {
		this.currentUserProvider = currentUserProvider;
		this.scenarioService = scenarioService;
		this.expenseService = expenseService;
		this.scoreSnapshotRepository = scoreSnapshotRepository;
		this.riskFactorRepository = riskFactorRepository;
		this.recommendationRepository = recommendationRepository;
		this.decisionEventService = decisionEventService;
		this.objectMapper = objectMapper;
	}

	@Transactional
	public ScenarioComparisonResponse compare(CompareScenariosRequest request) {
		List<Long> scenarioIds = request.scenarioIds().stream().distinct().toList();
		if (scenarioIds.size() < 2 || scenarioIds.size() > 4) {
			throw new IllegalArgumentException("Compare between 2 and 4 unique scenarios.");
		}
		List<ScenarioComparisonItemResponse> items = scenarioIds.stream()
				.map(this::buildItem)
				.sorted(Comparator.comparing(ScenarioComparisonItemResponse::decisionScore).reversed())
				.toList();
		ScenarioComparisonItemResponse recommended = items.get(0);
		String reason = recommendationReason(recommended);
		decisionEventService.saveEvent(
				currentUserProvider.currentUserId(),
				recommended.scenarioId(),
				null,
				DecisionType.SCENARIO_COMPARED,
				"Compare independence scenarios",
				recommended.name(),
				null,
				recommended.latestScore(),
				reason,
				toJson(Map.of("scenarioIds", scenarioIds, "recommendedScenarioId", recommended.scenarioId())));
		return new ScenarioComparisonResponse(items, recommended.scenarioId(), reason);
	}

	@Transactional
	public void selectScenario(Long scenarioId, SelectScenarioRequest request) {
		ScenarioEntity scenario = scenarioService.findCurrentUserScenario(scenarioId);
		ScoreSnapshotEntity latestScore = scoreSnapshotRepository.findFirstByScenarioIdOrderByCreatedAtDesc(scenarioId).orElse(null);
		decisionEventService.saveEvent(
				currentUserProvider.currentUserId(),
				scenario.getId(),
				null,
				DecisionType.SCENARIO_SELECTED,
				"Select scenario",
				scenario.getName(),
				null,
				latestScore == null ? null : latestScore.getScore(),
				request == null ? null : request.reason(),
				toJson(Map.of("scenarioId", scenario.getId(), "scenarioName", scenario.getName())));
	}

	private ScenarioComparisonItemResponse buildItem(Long scenarioId) {
		ScenarioEntity scenario = scenarioService.findCurrentUserScenario(scenarioId);
		List<ScenarioExpenseResponse> expenses = expenseService.listScenarioExpenses(scenarioId);
		BigDecimal monthlyExpenses = expenses.stream().map(ScenarioExpenseResponse::monthlyEquivalent).reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal estimatedSavings = scenario.getMonthlyIncome().subtract(monthlyExpenses);
		BigDecimal savingsRate = ratio(estimatedSavings, scenario.getMonthlyIncome());
		BigDecimal rentBurden = ratio(sumByCategory(expenses, "rent"), scenario.getMonthlyIncome());
		BigDecimal fixedBurden = ratio(expenses.stream()
				.filter(ScenarioExpenseResponse::isEssential)
				.map(ScenarioExpenseResponse::monthlyEquivalent)
				.reduce(BigDecimal.ZERO, BigDecimal::add), scenario.getMonthlyIncome());
		BigDecimal emergencyCoverage = ratio(scenario.getEmergencyFundCurrent(), monthlyExpenses);
		ScoreSnapshotEntity snapshot = scoreSnapshotRepository.findFirstByScenarioIdOrderByCreatedAtDesc(scenarioId).orElse(null);
		List<RiskFactorEntity> risks = snapshot == null ? List.of() : riskFactorRepository.findByScoreSnapshotIdOrderByIdAsc(snapshot.getId());
		RiskFactorEntity mainRisk = risks.stream().max(Comparator.comparing(risk -> severityRank(risk.getSeverity()))).orElse(null);
		String mainRecommendation = recommendationRepository
				.findByUserIdAndScenarioIdAndStatusOrderByPriorityAscSeverityDescCreatedAtDesc(currentUserProvider.currentUserId(), scenarioId, RecommendationStatus.OPEN)
				.stream()
				.findFirst()
				.map(recommendation -> recommendation.getTitle())
				.orElse(null);
		boolean scoreMissing = snapshot == null;
		boolean scoreStale = snapshot != null && snapshot.getCreatedAt().isBefore(Instant.now().minus(STALE_SCORE_AFTER));
		int decisionScore = decisionScore(snapshot, mainRisk, estimatedSavings, savingsRate, emergencyCoverage, scoreMissing, scoreStale);
		return new ScenarioComparisonItemResponse(
				scenario.getId(),
				scenario.getName(),
				scenario.getMonthlyIncome(),
				monthlyExpenses,
				estimatedSavings,
				savingsRate,
				rentBurden,
				fixedBurden,
				emergencyCoverage,
				snapshot == null ? null : snapshot.getScore(),
				snapshot == null ? null : snapshot.getStatus(),
				scoreMissing,
				scoreStale,
				mainRisk == null ? null : mainRisk.getSeverity(),
				mainRisk == null ? null : mainRisk.getTitle(),
				mainRecommendation,
				decisionScore);
	}

	private int decisionScore(
			ScoreSnapshotEntity snapshot,
			RiskFactorEntity mainRisk,
			BigDecimal estimatedSavings,
			BigDecimal savingsRate,
			BigDecimal emergencyCoverage,
			boolean scoreMissing,
			boolean scoreStale) {
		int value = snapshot == null ? 40 : snapshot.getScore();
		if (estimatedSavings.signum() < 0) value -= 35;
		if (savingsRate.compareTo(new BigDecimal("0.10")) >= 0) value += 10;
		if (savingsRate.compareTo(new BigDecimal("0.05")) < 0) value -= 15;
		if (emergencyCoverage.compareTo(new BigDecimal("2.00")) >= 0) value += 10;
		if (emergencyCoverage.compareTo(BigDecimal.ONE) < 0) value -= 10;
		if (mainRisk != null) {
			value -= switch (mainRisk.getSeverity()) {
				case CRITICAL -> 35;
				case HIGH -> 18;
				case MEDIUM -> 8;
				case LOW -> 2;
			};
		}
		if (scoreMissing) value -= 12;
		if (scoreStale) value -= 6;
		return value;
	}

	private String recommendationReason(ScenarioComparisonItemResponse item) {
		String risk = item.mainRisk() == null ? "no dominant risk" : "main risk: " + item.mainRisk();
		return "%s is the strongest option because it balances score %s, savings rate %s%%, emergency coverage %s months, and %s."
				.formatted(
						item.name(),
						item.latestScore() == null ? "missing" : item.latestScore(),
						percent(item.savingsRate()),
						item.emergencyCoverage(),
						risk);
	}

	private BigDecimal sumByCategory(List<ScenarioExpenseResponse> expenses, String categoryCode) {
		return expenses.stream()
				.filter(expense -> categoryCode.equals(expense.categoryCode()))
				.map(ScenarioExpenseResponse::monthlyEquivalent)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private BigDecimal ratio(BigDecimal numerator, BigDecimal denominator) {
		if (denominator == null || denominator.signum() <= 0) {
			return BigDecimal.ZERO;
		}
		return (numerator == null ? BigDecimal.ZERO : numerator).divide(denominator, 6, RoundingMode.HALF_UP);
	}

	private String percent(BigDecimal ratio) {
		return ratio.multiply(new BigDecimal("100")).setScale(1, RoundingMode.HALF_UP).toPlainString();
	}

	private int severityRank(RiskSeverity severity) {
		return switch (severity) {
			case CRITICAL -> 4;
			case HIGH -> 3;
			case MEDIUM -> 2;
			case LOW -> 1;
		};
	}

	private String toJson(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException exception) {
			return "{\"error\":\"Could not serialize context\"}";
		}
	}
}
