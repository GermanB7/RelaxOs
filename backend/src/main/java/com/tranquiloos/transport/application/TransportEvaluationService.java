package com.tranquiloos.transport.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.decisions.application.DecisionEventService;
import com.tranquiloos.decisions.domain.DecisionType;
import com.tranquiloos.expenses.api.ScenarioExpenseResponse;
import com.tranquiloos.expenses.application.ExpenseService;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationStatus;
import com.tranquiloos.recommendations.domain.RecommendationType;
import com.tranquiloos.recommendations.infrastructure.RecommendationEntity;
import com.tranquiloos.recommendations.infrastructure.RecommendationJpaRepository;
import com.tranquiloos.scenarios.application.ScenarioService;
import com.tranquiloos.scenarios.infrastructure.ScenarioEntity;
import com.tranquiloos.shared.error.ResourceNotFoundException;
import com.tranquiloos.transport.api.EvaluatedTransportOptionResponse;
import com.tranquiloos.transport.api.TransportEvaluationResponse;
import com.tranquiloos.transport.api.TransportOptionRequest;
import com.tranquiloos.transport.api.TransportOptionResponse;
import com.tranquiloos.transport.domain.TransportOptionType;
import com.tranquiloos.transport.domain.TransportRiskLevel;
import com.tranquiloos.transport.infrastructure.TransportEvaluationEntity;
import com.tranquiloos.transport.infrastructure.TransportEvaluationRepository;
import com.tranquiloos.transport.infrastructure.TransportOptionEntity;
import com.tranquiloos.transport.infrastructure.TransportOptionRepository;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransportEvaluationService {

	private final CurrentUserProvider currentUserProvider;
	private final ScenarioService scenarioService;
	private final ExpenseService expenseService;
	private final TransportOptionRepository optionRepository;
	private final TransportEvaluationRepository evaluationRepository;
	private final RecommendationJpaRepository recommendationRepository;
	private final DecisionEventService decisionEventService;
	private final ObjectMapper objectMapper;

	public TransportEvaluationService(
			CurrentUserProvider currentUserProvider,
			ScenarioService scenarioService,
			ExpenseService expenseService,
			TransportOptionRepository optionRepository,
			TransportEvaluationRepository evaluationRepository,
			RecommendationJpaRepository recommendationRepository,
			DecisionEventService decisionEventService,
			ObjectMapper objectMapper) {
		this.currentUserProvider = currentUserProvider;
		this.scenarioService = scenarioService;
		this.expenseService = expenseService;
		this.optionRepository = optionRepository;
		this.evaluationRepository = evaluationRepository;
		this.recommendationRepository = recommendationRepository;
		this.decisionEventService = decisionEventService;
		this.objectMapper = objectMapper;
	}

	@Transactional
	public TransportOptionResponse createOption(Long scenarioId, TransportOptionRequest request) {
		scenarioService.findCurrentUserScenario(scenarioId);
		TransportOptionEntity entity = new TransportOptionEntity();
		entity.setScenarioId(scenarioId);
		apply(entity, request);
		return toResponse(optionRepository.save(entity));
	}

	@Transactional(readOnly = true)
	public List<TransportOptionResponse> listOptions(Long scenarioId) {
		scenarioService.findCurrentUserScenario(scenarioId);
		return optionRepository.findByScenarioIdOrderByIdAsc(scenarioId).stream().map(this::toResponse).toList();
	}

	@Transactional
	public TransportOptionResponse updateOption(Long optionId, TransportOptionRequest request) {
		TransportOptionEntity entity = optionRepository.findById(optionId)
				.orElseThrow(() -> new ResourceNotFoundException("Transport option was not found"));
		scenarioService.findCurrentUserScenario(entity.getScenarioId());
		apply(entity, request);
		return toResponse(optionRepository.save(entity));
	}

	@Transactional
	public void deleteOption(Long optionId) {
		TransportOptionEntity entity = optionRepository.findById(optionId)
				.orElseThrow(() -> new ResourceNotFoundException("Transport option was not found"));
		scenarioService.findCurrentUserScenario(entity.getScenarioId());
		optionRepository.delete(entity);
	}

	@Transactional
	public TransportEvaluationResponse evaluate(Long scenarioId) {
		ScenarioEntity scenario = scenarioService.findCurrentUserScenario(scenarioId);
		List<TransportOptionEntity> options = optionRepository.findByScenarioIdOrderByIdAsc(scenarioId);
		if (options.isEmpty()) {
			throw new IllegalArgumentException("Add at least one transport option before evaluating.");
		}
		BigDecimal monthlyExpensesWithoutTransport = expenseService.listScenarioExpenses(scenarioId).stream()
				.map(ScenarioExpenseResponse::monthlyEquivalent)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal emergencyCoverage = ratio(scenario.getEmergencyFundCurrent(), monthlyExpensesWithoutTransport);
		List<EvaluatedTransportOptionResponse> evaluatedOptions = options.stream()
				.map(option -> evaluateOption(option, scenario, monthlyExpensesWithoutTransport, emergencyCoverage))
				.sorted(Comparator.comparing(EvaluatedTransportOptionResponse::fitScore).reversed())
				.toList();
		EvaluatedTransportOptionResponse best = evaluatedOptions.get(0);
		TransportOptionType future = futureOption(options, best, emergencyCoverage);
		TransportRiskLevel riskLevel = riskLevel(best.transportBurden());
		String conditions = conditionsToSwitch(future, emergencyCoverage);
		TransportEvaluationEntity entity = new TransportEvaluationEntity();
		entity.setUserId(currentUserProvider.currentUserId());
		entity.setScenarioId(scenarioId);
		entity.setRecommendedCurrentOption(best.optionType());
		entity.setFutureViableOption(future);
		entity.setTransportBurden(best.transportBurden());
		entity.setFitScore(best.fitScore());
		entity.setRiskLevel(riskLevel);
		entity.setExplanation(best.explanation());
		entity.setConditionsToSwitch(conditions);
		entity.setEvaluatedOptionsJson(toJson(evaluatedOptions));
		TransportEvaluationEntity saved = evaluationRepository.save(entity);
		createTransportRecommendations(scenario, best, evaluatedOptions, emergencyCoverage, monthlyExpensesWithoutTransport);
		decisionEventService.saveEvent(
				currentUserProvider.currentUserId(),
				scenarioId,
				null,
				DecisionType.TRANSPORT_EVALUATED,
				"Evaluate transport options",
				best.optionType().name(),
				null,
				best.fitScore(),
				best.explanation(),
				toJson(Map.of("recommendedCurrentOption", best.optionType(), "futureViableOption", future, "transportBurden", best.transportBurden())));
		return toResponse(saved, evaluatedOptions);
	}

	@Transactional(readOnly = true)
	public TransportEvaluationResponse latest(Long scenarioId) {
		scenarioService.findCurrentUserScenario(scenarioId);
		TransportEvaluationEntity entity = evaluationRepository.findFirstByScenarioIdOrderByCreatedAtDesc(scenarioId)
				.orElseThrow(() -> new ResourceNotFoundException("Transport evaluation was not found"));
		return toResponse(entity, evaluatedOptions(entity.getEvaluatedOptionsJson()));
	}

	private EvaluatedTransportOptionResponse evaluateOption(
			TransportOptionEntity option,
			ScenarioEntity scenario,
			BigDecimal monthlyExpensesWithoutTransport,
			BigDecimal emergencyCoverage) {
		BigDecimal totalCost = totalMonthlyCost(option);
		BigDecimal burden = ratio(totalCost, scenario.getMonthlyIncome());
		BigDecimal savingsAfterTransport = scenario.getMonthlyIncome().subtract(monthlyExpensesWithoutTransport).subtract(totalCost);
		BigDecimal savingsRateAfterTransport = ratio(savingsAfterTransport, scenario.getMonthlyIncome());
		int fitScore = 100;
		fitScore -= burden.multiply(BigDecimal.valueOf(200)).intValue();
		fitScore += (option.getComfortScore() - 3) * 5;
		fitScore += (option.getSafetyScore() - 3) * 8;
		fitScore += (option.getFlexibilityScore() - 3) * 5;
		if (option.getAverageTimeMinutes() > 90) fitScore -= 15;
		if (option.getOptionType() == TransportOptionType.MOTORCYCLE && emergencyCoverage.compareTo(new BigDecimal("2.00")) < 0) fitScore -= 35;
		if (option.getOptionType() == TransportOptionType.CAR && savingsRateAfterTransport.compareTo(new BigDecimal("0.05")) < 0) fitScore -= 35;
		if ((option.getOptionType() == TransportOptionType.BICYCLE || option.getOptionType() == TransportOptionType.WALKING) && option.getSafetyScore() <= 2) fitScore -= 30;
		fitScore = Math.max(1, Math.min(100, fitScore));
		String explanation = explanation(option, burden, savingsRateAfterTransport, emergencyCoverage);
		return new EvaluatedTransportOptionResponse(option.getId(), option.getOptionType(), totalCost, burden, fitScore, riskLevel(burden).name(), explanation);
	}

	private String explanation(
			TransportOptionEntity option,
			BigDecimal burden,
			BigDecimal savingsRateAfterTransport,
			BigDecimal emergencyCoverage) {
		if (option.getOptionType() == TransportOptionType.MOTORCYCLE && emergencyCoverage.compareTo(new BigDecimal("2.00")) < 0) {
			return "Motorcycle is not recommended now because emergency coverage is below 2 months.";
		}
		if (option.getOptionType() == TransportOptionType.CAR && savingsRateAfterTransport.compareTo(new BigDecimal("0.05")) < 0) {
			return "Car is not recommended because savings after transport would fall below 5%.";
		}
		if ((option.getOptionType() == TransportOptionType.BICYCLE || option.getOptionType() == TransportOptionType.WALKING) && option.getSafetyScore() <= 2) {
			return "This low-cost option is limited by safety risk.";
		}
		if (burden.compareTo(new BigDecimal("0.15")) > 0) {
			return "Transport cost is high relative to monthly income.";
		}
		return "This option balances cost, time, safety, comfort, and flexibility for the current scenario.";
	}

	private TransportOptionType futureOption(List<TransportOptionEntity> options, EvaluatedTransportOptionResponse best, BigDecimal emergencyCoverage) {
		boolean expensiveUber = options.stream().anyMatch(option -> option.getOptionType() == TransportOptionType.UBER_DIDI
				&& option.getTripsPerWeek() >= 10
				&& totalMonthlyCost(option).compareTo(new BigDecimal("0")) > 0);
		if (expensiveUber && emergencyCoverage.compareTo(new BigDecimal("2.00")) >= 0) {
			return TransportOptionType.MOTORCYCLE;
		}
		boolean slowPublicTransport = options.stream().anyMatch(option -> option.getOptionType() == TransportOptionType.PUBLIC_TRANSPORT
				&& option.getAverageTimeMinutes() > 75);
		if (slowPublicTransport) {
			return TransportOptionType.MIXED;
		}
		return best.optionType();
	}

	private void createTransportRecommendations(
			ScenarioEntity scenario,
			EvaluatedTransportOptionResponse best,
			List<EvaluatedTransportOptionResponse> evaluatedOptions,
			BigDecimal emergencyCoverage,
			BigDecimal monthlyExpensesWithoutTransport) {
		if (best.transportBurden().compareTo(new BigDecimal("0.15")) > 0) {
			upsertRecommendation(scenario, "TRANSPORT_COST_HIGH", RecommendationSeverity.HIGH, "Reduce transport burden", "Transport costs are above 15% of monthly income. Revisit frequency, mixed routes, or lower-cost alternatives.");
		}
		evaluatedOptions.stream().filter(option -> option.optionType() == TransportOptionType.MOTORCYCLE && emergencyCoverage.compareTo(new BigDecimal("2.00")) < 0)
				.findFirst()
				.ifPresent(option -> upsertRecommendation(scenario, "MOTORCYCLE_NOT_READY", RecommendationSeverity.MEDIUM, "Motorcycle is not ready yet", "Build at least 2 months of emergency coverage before depending on motorcycle ownership."));
		evaluatedOptions.stream().filter(option -> option.optionType() == TransportOptionType.CAR)
				.findFirst()
				.ifPresent(option -> {
					BigDecimal savingsAfterCar = scenario.getMonthlyIncome().subtract(monthlyExpensesWithoutTransport).subtract(option.totalMonthlyCost());
					if (ratio(savingsAfterCar, scenario.getMonthlyIncome()).compareTo(new BigDecimal("0.05")) < 0) {
						upsertRecommendation(scenario, "CAR_NOT_READY", RecommendationSeverity.HIGH, "Car is not financially ready", "Car costs would push savings below 5% of income.");
					}
				});
		evaluatedOptions.stream().filter(option -> option.optionType() == TransportOptionType.UBER_DIDI && option.transportBurden().compareTo(new BigDecimal("0.12")) > 0)
				.findFirst()
				.ifPresent(option -> upsertRecommendation(scenario, "UBER_TOO_EXPENSIVE", RecommendationSeverity.MEDIUM, "Uber/Didi pressure is high", "Frequent ride-hailing is creating transport pressure. Consider a mixed plan or a future ownership option after funding conditions are met."));
		evaluatedOptions.stream().filter(option -> option.optionType() == TransportOptionType.MIXED)
				.findFirst()
				.ifPresent(option -> upsertRecommendation(scenario, "MIXED_TRANSPORT_RECOMMENDED", RecommendationSeverity.LOW, "Mixed transport may fit better", "A mixed option can reduce time pressure without committing to car or motorcycle ownership."));
		if (best.transportBurden().compareTo(new BigDecimal("0.10")) <= 0 && TransportRiskLevel.LOW.name().equals(best.riskLevel())) {
			upsertRecommendation(scenario, "TRANSPORT_OPTION_STABLE", RecommendationSeverity.LOW, "Transport option is stable", "Current transport burden looks sustainable for this scenario.");
		}
	}

	private void upsertRecommendation(ScenarioEntity scenario, String ruleKey, RecommendationSeverity severity, String title, String message) {
		Long userId = currentUserProvider.currentUserId();
		RecommendationEntity entity = recommendationRepository
				.findByUserIdAndScenarioIdAndSourceRuleKeyAndStatus(userId, scenario.getId(), ruleKey, RecommendationStatus.OPEN)
				.orElseGet(RecommendationEntity::new);
		entity.setUserId(userId);
		entity.setScenarioId(scenario.getId());
		entity.setType(RecommendationType.TRANSPORT);
		entity.setSeverity(severity);
		entity.setPriority(severity == RecommendationSeverity.HIGH ? 25 : 55);
		entity.setTitle(title);
		entity.setMessage(message);
		entity.setActionLabel("Open transport");
		entity.setActionType("OPEN_TRANSPORT");
		entity.setSourceRuleKey(ruleKey);
		entity.setStatus(RecommendationStatus.OPEN);
		entity.setContextJson(toJson(Map.of("ruleKey", ruleKey)));
		recommendationRepository.save(entity);
	}

	private void apply(TransportOptionEntity entity, TransportOptionRequest request) {
		entity.setOptionType(request.optionType());
		entity.setMonthlyCost(request.monthlyCost());
		entity.setTripsPerWeek(request.tripsPerWeek());
		entity.setAverageTimeMinutes(request.averageTimeMinutes());
		entity.setComfortScore(request.comfortScore());
		entity.setSafetyScore(request.safetyScore());
		entity.setFlexibilityScore(request.flexibilityScore());
		entity.setParkingCost(request.parkingCost());
		entity.setMaintenanceCost(request.maintenanceCost());
		entity.setInsuranceCost(request.insuranceCost());
		entity.setFuelCost(request.fuelCost());
		entity.setUpfrontCost(request.upfrontCost());
		entity.setHasParking(request.hasParking());
		entity.setHasLicense(request.hasLicense());
		entity.setNotes(request.notes());
	}

	private TransportOptionResponse toResponse(TransportOptionEntity entity) {
		return new TransportOptionResponse(
				entity.getId(),
				entity.getScenarioId(),
				entity.getOptionType(),
				entity.getMonthlyCost(),
				totalMonthlyCost(entity),
				entity.getTripsPerWeek(),
				entity.getAverageTimeMinutes(),
				entity.getComfortScore(),
				entity.getSafetyScore(),
				entity.getFlexibilityScore(),
				entity.getParkingCost(),
				entity.getMaintenanceCost(),
				entity.getInsuranceCost(),
				entity.getFuelCost(),
				entity.getUpfrontCost(),
				entity.getHasParking(),
				entity.getHasLicense(),
				entity.getNotes(),
				entity.getCreatedAt(),
				entity.getUpdatedAt());
	}

	private TransportEvaluationResponse toResponse(TransportEvaluationEntity entity, List<EvaluatedTransportOptionResponse> evaluatedOptions) {
		return new TransportEvaluationResponse(
				entity.getId(),
				entity.getScenarioId(),
				entity.getRecommendedCurrentOption(),
				entity.getFutureViableOption(),
				entity.getTransportBurden(),
				entity.getFitScore(),
				entity.getRiskLevel(),
				entity.getExplanation(),
				entity.getConditionsToSwitch(),
				evaluatedOptions,
				entity.getCreatedAt());
	}

	private List<EvaluatedTransportOptionResponse> evaluatedOptions(String json) {
		try {
			return objectMapper.readValue(json, new TypeReference<>() {});
		} catch (Exception exception) {
			return List.of();
		}
	}

	private BigDecimal totalMonthlyCost(TransportOptionEntity option) {
		return zero(option.getMonthlyCost())
				.add(zero(option.getParkingCost()))
				.add(zero(option.getMaintenanceCost()))
				.add(zero(option.getInsuranceCost()))
				.add(zero(option.getFuelCost()));
	}

	private BigDecimal zero(BigDecimal value) {
		return value == null ? BigDecimal.ZERO : value;
	}

	private BigDecimal ratio(BigDecimal numerator, BigDecimal denominator) {
		if (denominator == null || denominator.signum() <= 0) {
			return BigDecimal.ZERO;
		}
		return zero(numerator).divide(denominator, 6, RoundingMode.HALF_UP);
	}

	private TransportRiskLevel riskLevel(BigDecimal burden) {
		if (burden.compareTo(new BigDecimal("0.20")) > 0) return TransportRiskLevel.CRITICAL;
		if (burden.compareTo(new BigDecimal("0.15")) > 0) return TransportRiskLevel.HIGH;
		if (burden.compareTo(new BigDecimal("0.10")) > 0) return TransportRiskLevel.MEDIUM;
		return TransportRiskLevel.LOW;
	}

	private String conditionsToSwitch(TransportOptionType future, BigDecimal emergencyCoverage) {
		if (future == TransportOptionType.MOTORCYCLE) {
			return emergencyCoverage.compareTo(new BigDecimal("2.00")) >= 0
					? "Validate license, protective gear, maintenance budget, and parking before switching."
					: "Reach at least 2 months of emergency coverage before considering motorcycle ownership.";
		}
		if (future == TransportOptionType.MIXED) {
			return "Use mixed transport on high-time days and keep public transport for predictable low-cost routes.";
		}
		return "Keep monitoring monthly burden, safety, and time pressure.";
	}

	private String toJson(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException exception) {
			return "{\"error\":\"Could not serialize context\"}";
		}
	}
}
