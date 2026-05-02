package com.tranquiloos.recommendations.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.expenses.api.ScenarioExpenseResponse;
import com.tranquiloos.expenses.application.ExpenseService;
import com.tranquiloos.recommendations.api.RecalculateRecommendationsResponse;
import com.tranquiloos.recommendations.api.RecommendationResponse;
import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationStatus;
import com.tranquiloos.recommendations.domain.rules.DataQualityRecommendationRule;
import com.tranquiloos.recommendations.domain.rules.FoodDeliveryPressureRecommendationRule;
import com.tranquiloos.recommendations.domain.rules.HighDebtBurdenRecommendationRule;
import com.tranquiloos.recommendations.domain.rules.HighFixedBurdenRecommendationRule;
import com.tranquiloos.recommendations.domain.rules.HighRentBurdenRecommendationRule;
import com.tranquiloos.recommendations.domain.rules.LowEmergencyFundRecommendationRule;
import com.tranquiloos.recommendations.domain.rules.NegativeMarginRecommendationRule;
import com.tranquiloos.recommendations.infrastructure.RecommendationEntity;
import com.tranquiloos.recommendations.infrastructure.RecommendationJpaRepository;
import com.tranquiloos.scenarios.application.ScenarioService;
import com.tranquiloos.scenarios.infrastructure.ScenarioEntity;
import com.tranquiloos.scoring.infrastructure.RiskFactorRepository;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotEntity;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotRepository;
import com.tranquiloos.shared.error.ResourceConflictException;
import com.tranquiloos.shared.error.ResourceNotFoundException;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecommendationEngineService {

	private final CurrentUserProvider currentUserProvider;
	private final ScenarioService scenarioService;
	private final ExpenseService expenseService;
	private final ScoreSnapshotRepository scoreSnapshotRepository;
	private final RiskFactorRepository riskFactorRepository;
	private final RecommendationJpaRepository recommendationRepository;
	private final RecommendationMapper mapper;
	private final ObjectMapper objectMapper;
	private final List<RecommendationRule> rules = List.of(
			new NegativeMarginRecommendationRule(),
			new LowEmergencyFundRecommendationRule(),
			new HighRentBurdenRecommendationRule(),
			new HighFixedBurdenRecommendationRule(),
			new HighDebtBurdenRecommendationRule(),
			new FoodDeliveryPressureRecommendationRule(),
			new DataQualityRecommendationRule());

	public RecommendationEngineService(
			CurrentUserProvider currentUserProvider,
			ScenarioService scenarioService,
			ExpenseService expenseService,
			ScoreSnapshotRepository scoreSnapshotRepository,
			RiskFactorRepository riskFactorRepository,
			RecommendationJpaRepository recommendationRepository,
			RecommendationMapper mapper,
			ObjectMapper objectMapper) {
		this.currentUserProvider = currentUserProvider;
		this.scenarioService = scenarioService;
		this.expenseService = expenseService;
		this.scoreSnapshotRepository = scoreSnapshotRepository;
		this.riskFactorRepository = riskFactorRepository;
		this.recommendationRepository = recommendationRepository;
		this.mapper = mapper;
		this.objectMapper = objectMapper;
	}

	@Transactional
	public RecalculateRecommendationsResponse recalculate(Long requestedScenarioId) {
		Long userId = currentUserProvider.currentUserId();
		Long scenarioId = requestedScenarioId == null ? latestScenarioId() : requestedScenarioId;
		ScenarioEntity scenario = scenarioService.findCurrentUserScenario(scenarioId);
		ScoreSnapshotEntity snapshot = scoreSnapshotRepository.findFirstByScenarioIdOrderByCreatedAtDesc(scenario.getId())
				.orElseThrow(() -> new ResourceConflictException("Calculate score before generating recommendations."));
		RecommendationContext context = buildContext(userId, scenario, snapshot);
		List<RecommendationResponse> responses = rules.stream()
				.flatMap(rule -> rule.evaluate(context).stream())
				.map(candidate -> upsertRecommendation(context, candidate))
				.map(mapper::toResponse)
				.toList();
		return new RecalculateRecommendationsResponse(scenario.getId(), responses.size(), responses);
	}

	private Long latestScenarioId() {
		return scenarioService.listCurrentUserScenarios()
				.stream()
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Scenario was not found"))
				.id();
	}

	private RecommendationContext buildContext(Long userId, ScenarioEntity scenario, ScoreSnapshotEntity snapshot) {
		List<ScenarioExpenseResponse> expenses = expenseService.listScenarioExpenses(scenario.getId());
		BigDecimal totalMonthlyExpenses = expenses.stream().map(ScenarioExpenseResponse::monthlyEquivalent).reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal available = scenario.getMonthlyIncome().subtract(totalMonthlyExpenses);
		BigDecimal rentMonthly = sumByCategory(expenses, "rent");
		BigDecimal debtMonthly = sumByCategory(expenses, "debt");
		BigDecimal foodDeliveryMonthly = sumByCategory(expenses, "food_delivery");
		BigDecimal essentialMonthly = expenses.stream().filter(ScenarioExpenseResponse::isEssential).map(ScenarioExpenseResponse::monthlyEquivalent).reduce(BigDecimal.ZERO, BigDecimal::add);
		List<String> riskKeys = riskFactorRepository.findByScoreSnapshotIdOrderByIdAsc(snapshot.getId()).stream().map(risk -> risk.getRiskKey()).toList();

		return new RecommendationContext(
				userId,
				scenario.getId(),
				snapshot.getId(),
				snapshot.getScore(),
				snapshot.getStatus(),
				snapshot.getConfidenceLevel(),
				scenario.getMonthlyIncome(),
				totalMonthlyExpenses,
				available,
				riskKeys,
				expenses,
				rentMonthly,
				debtMonthly,
				foodDeliveryMonthly,
				essentialMonthly,
				ratio(rentMonthly, scenario.getMonthlyIncome()),
				ratio(essentialMonthly, scenario.getMonthlyIncome()),
				ratio(debtMonthly, scenario.getMonthlyIncome()),
				ratio(foodDeliveryMonthly, scenario.getMonthlyIncome()),
				ratio(scenario.getEmergencyFundCurrent(), totalMonthlyExpenses),
				ratio(available, scenario.getMonthlyIncome()));
	}

	private RecommendationEntity upsertRecommendation(RecommendationContext context, RecommendationCandidate candidate) {
		RecommendationEntity entity = recommendationRepository
				.findByUserIdAndScenarioIdAndSourceRuleKeyAndStatus(context.userId(), context.scenarioId(), candidate.sourceRuleKey(), RecommendationStatus.OPEN)
				.orElseGet(RecommendationEntity::new);
		entity.setUserId(context.userId());
		entity.setScenarioId(context.scenarioId());
		entity.setScoreSnapshotId(context.scoreSnapshotId());
		entity.setType(candidate.type());
		entity.setSeverity(candidate.severity());
		entity.setPriority(candidate.priority());
		entity.setTitle(candidate.title());
		entity.setMessage(candidate.message());
		entity.setActionLabel(candidate.actionLabel());
		entity.setActionType(candidate.actionType());
		entity.setSourceRuleKey(candidate.sourceRuleKey());
		entity.setStatus(RecommendationStatus.OPEN);
		entity.setContextJson(toJson(Map.of(
				"score", context.score(),
				"scoreStatus", context.scoreStatus().name(),
				"confidenceLevel", context.confidenceLevel().name(),
				"rentBurden", context.rentBurden(),
				"fixedBurden", context.fixedBurden(),
				"debtBurden", context.debtBurden(),
				"foodDeliveryBurden", context.foodDeliveryBurden(),
				"emergencyCoverageMonths", context.emergencyCoverageMonths(),
				"savingsRate", context.savingsRate())));
		return recommendationRepository.save(entity);
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

	private String toJson(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException exception) {
			return "{}";
		}
	}
}
