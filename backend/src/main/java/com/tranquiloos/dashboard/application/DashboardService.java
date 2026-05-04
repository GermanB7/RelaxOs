package com.tranquiloos.dashboard.application;

import java.util.Comparator;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.tranquiloos.dashboard.api.DashboardSummaryResponse;
import com.tranquiloos.dashboard.api.DashboardActiveModeResponse;
import com.tranquiloos.dashboard.api.DashboardHomeSetupResponse;
import com.tranquiloos.dashboard.api.DashboardMealPlannerResponse;
import com.tranquiloos.dashboard.api.DashboardProfileResponse;
import com.tranquiloos.dashboard.api.DashboardRecommendationResponse;
import com.tranquiloos.dashboard.api.DashboardResponse;
import com.tranquiloos.dashboard.api.DashboardRiskResponse;
import com.tranquiloos.dashboard.api.DashboardScenarioSummaryResponse;
import com.tranquiloos.dashboard.api.DashboardScoreResponse;
import com.tranquiloos.home.api.HomeSetupSummaryResponse;
import com.tranquiloos.home.application.HomeSetupService;
import com.tranquiloos.modes.api.ActiveModeSummaryResponse;
import com.tranquiloos.modes.application.ActiveModeProvider;
import com.tranquiloos.recommendations.domain.RecommendationStatus;
import com.tranquiloos.recommendations.infrastructure.RecommendationEntity;
import com.tranquiloos.recommendations.infrastructure.RecommendationJpaRepository;
import com.tranquiloos.recommendations.infrastructure.DecisionEventEntity;
import com.tranquiloos.recommendations.infrastructure.DecisionEventJpaRepository;
import com.tranquiloos.scenarios.api.ScenarioResponse;
import com.tranquiloos.scenarios.api.ScenarioSummaryResponse;
import com.tranquiloos.scenarios.application.ScenarioService;
import com.tranquiloos.scoring.domain.RiskSeverity;
import com.tranquiloos.scoring.infrastructure.RiskFactorEntity;
import com.tranquiloos.scoring.infrastructure.RiskFactorRepository;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotEntity;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotRepository;
import com.tranquiloos.transport.infrastructure.TransportEvaluationEntity;
import com.tranquiloos.transport.infrastructure.TransportEvaluationRepository;
import com.tranquiloos.users.api.ProfileResponse;
import com.tranquiloos.users.application.CurrentUserProvider;
import com.tranquiloos.users.application.ProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

	private final CurrentUserProvider currentUserProvider;
	private final ProfileService profileService;
	private final ScenarioService scenarioService;
	private final ScoreSnapshotRepository scoreSnapshotRepository;
	private final RiskFactorRepository riskFactorRepository;
	private final RecommendationJpaRepository recommendationRepository;
	private final ActiveModeProvider activeModeProvider;
	private final HomeSetupService homeSetupService;
	private final DecisionEventJpaRepository decisionEventRepository;
	private final TransportEvaluationRepository transportEvaluationRepository;

	public DashboardService(
			CurrentUserProvider currentUserProvider,
			ProfileService profileService,
			ScenarioService scenarioService,
			ScoreSnapshotRepository scoreSnapshotRepository,
			RiskFactorRepository riskFactorRepository,
			RecommendationJpaRepository recommendationRepository,
			ActiveModeProvider activeModeProvider,
			HomeSetupService homeSetupService,
			DecisionEventJpaRepository decisionEventRepository,
			TransportEvaluationRepository transportEvaluationRepository) {
		this.currentUserProvider = currentUserProvider;
		this.profileService = profileService;
		this.scenarioService = scenarioService;
		this.scoreSnapshotRepository = scoreSnapshotRepository;
		this.riskFactorRepository = riskFactorRepository;
		this.recommendationRepository = recommendationRepository;
		this.activeModeProvider = activeModeProvider;
		this.homeSetupService = homeSetupService;
		this.decisionEventRepository = decisionEventRepository;
		this.transportEvaluationRepository = transportEvaluationRepository;
	}

	@Transactional(readOnly = true)
	public DashboardResponse getDashboard() {
		Long userId = currentUserProvider.currentUserId();
		ProfileResponse profile = profileService.getCurrentProfile();
		List<ScenarioResponse> scenarios = scenarioService.listCurrentUserScenarios();
		ScenarioResponse primaryScenario = scenarios.isEmpty() ? null : scenarios.get(0);
		ScoreSnapshotEntity latestScore = primaryScenario == null ? null : scoreSnapshotRepository
				.findFirstByScenarioIdOrderByCreatedAtDesc(primaryScenario.id())
				.orElse(null);

		return new DashboardResponse(
				toProfile(profile),
				toPrimaryScenario(primaryScenario),
				toScore(latestScore),
				toTopRisks(latestScore),
				toTopRecommendations(userId),
				toActiveMode(activeModeProvider.currentSummary()),
				toHomeSetup(userId, primaryScenario),
				new DashboardMealPlannerResponse("Need a low-effort meal?"));
	}

	@Transactional(readOnly = true)
	public DashboardSummaryResponse getDashboardSummary() {
		Long userId = currentUserProvider.currentUserId();
		List<ScenarioResponse> scenarios = scenarioService.listCurrentUserScenarios();
		ScenarioResponse activeScenario = scenarios.stream()
				.filter(scenario -> "ACTIVE".equals(scenario.status().name()))
				.findFirst()
				.orElse(scenarios.isEmpty() ? null : scenarios.get(0));
		ScenarioSummaryResponse scenarioSummary = activeScenario == null ? null : scenarioService.getSummary(activeScenario.id());
		ScoreSnapshotEntity latestScore = activeScenario == null ? null : scoreSnapshotRepository
				.findFirstByScenarioIdOrderByCreatedAtDesc(activeScenario.id())
				.orElse(null);
		List<RiskFactorEntity> risks = latestScore == null ? List.of() : riskFactorRepository
				.findByScoreSnapshotIdOrderByIdAsc(latestScore.getId())
				.stream()
				.sorted(Comparator.comparing((RiskFactorEntity risk) -> severityRank(risk.getSeverity())).reversed())
				.toList();
		List<RecommendationEntity> recommendations = recommendationRepository
				.findTop5ByUserIdAndStatusOrderByPriorityAscSeverityDescCreatedAtDesc(userId, RecommendationStatus.OPEN);
		ActiveModeSummaryResponse activeMode = activeModeProvider.currentSummary();
		HomeSetupSummaryResponse homeSummary = homeSetupService.getSummary(userId, activeScenario == null ? null : activeScenario.id());
		TransportEvaluationEntity latestTransport = transportEvaluationRepository.findFirstByUserIdOrderByCreatedAtDesc(userId).orElse(null);
		List<DecisionEventEntity> recentEvents = decisionEventRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);

		return new DashboardSummaryResponse(
				activeScenario == null ? null : new DashboardSummaryResponse.ActiveScenario(activeScenario.id(), activeScenario.name()),
				latestScore == null ? null : latestScore.getScore(),
				latestScore == null ? null : latestScore.getStatus(),
				toSummaryActiveMode(activeMode),
				toFinancialSnapshot(activeScenario, scenarioSummary, latestTransport),
				risks.stream().findFirst().map(this::toMainRisk).orElse(null),
				recommendations.stream().limit(3).map(this::toSummaryRecommendation).toList(),
				toComparisonSummary(scenarios.size(), recentEvents),
				toTransportSummary(latestTransport),
				toHomePriority(homeSummary),
				toMealSuggestion(activeMode),
				recentEvents.stream().map(this::toDecisionEventSummary).toList(),
				quickActions(activeScenario, latestScore, risks, recommendations, homeSummary, latestTransport));
	}

	private DashboardProfileResponse toProfile(ProfileResponse profile) {
		return new DashboardProfileResponse(profile.displayName(), profile.city(), profile.currency());
	}

	private DashboardScenarioSummaryResponse toPrimaryScenario(ScenarioResponse scenario) {
		if (scenario == null) {
			return null;
		}
		ScenarioSummaryResponse summary = scenarioService.getSummary(scenario.id());
		return new DashboardScenarioSummaryResponse(
				scenario.id(),
				scenario.name(),
				summary.monthlyIncome(),
				summary.totalMonthlyExpenses(),
				summary.estimatedMonthlyAvailable());
	}

	private DashboardScoreResponse toScore(ScoreSnapshotEntity latestScore) {
		if (latestScore == null) {
			return null;
		}
		return new DashboardScoreResponse(latestScore.getScore(), latestScore.getStatus(), latestScore.getSummary());
	}

	private List<DashboardRiskResponse> toTopRisks(ScoreSnapshotEntity latestScore) {
		if (latestScore == null) {
			return List.of();
		}
		return riskFactorRepository.findByScoreSnapshotIdOrderByIdAsc(latestScore.getId())
				.stream()
				.sorted(Comparator.comparing((RiskFactorEntity risk) -> severityRank(risk.getSeverity())).reversed())
				.limit(3)
				.map(risk -> new DashboardRiskResponse(risk.getSeverity(), risk.getTitle()))
				.toList();
	}

	private List<DashboardRecommendationResponse> toTopRecommendations(Long userId) {
		return recommendationRepository.findTop5ByUserIdAndStatusOrderByPriorityAscSeverityDescCreatedAtDesc(userId, RecommendationStatus.OPEN)
				.stream()
				.limit(3)
				.map(this::toRecommendation)
				.toList();
	}

	private DashboardRecommendationResponse toRecommendation(RecommendationEntity recommendation) {
		return new DashboardRecommendationResponse(
				recommendation.getId(),
				recommendation.getScenarioId(),
				recommendation.getSeverity(),
				recommendation.getTitle(),
				recommendation.getActionLabel(),
				recommendation.getActionType());
	}

	private DashboardActiveModeResponse toActiveMode(ActiveModeSummaryResponse activeMode) {
		return new DashboardActiveModeResponse(
				activeMode.hasActiveMode(),
				activeMode.modeCode(),
				activeMode.modeName(),
				activeMode.scenarioId(),
				activeMode.guidance());
	}

	private DashboardHomeSetupResponse toHomeSetup(Long userId, ScenarioResponse primaryScenario) {
		Long scenarioId = primaryScenario == null ? null : primaryScenario.id();
		HomeSetupSummaryResponse summary = homeSetupService.getSummary(userId, scenarioId);
		return new DashboardHomeSetupResponse(
				summary.totalItems() > 0,
				summary.tier1CompletionPercentage(),
				summary.nextBestPurchase() == null ? null : summary.nextBestPurchase().name(),
				summary.pendingItems());
	}

	private int severityRank(RiskSeverity severity) {
		return switch (severity) {
			case CRITICAL -> 4;
			case HIGH -> 3;
			case MEDIUM -> 2;
			case LOW -> 1;
		};
	}

	private DashboardSummaryResponse.ActiveMode toSummaryActiveMode(ActiveModeSummaryResponse activeMode) {
		return new DashboardSummaryResponse.ActiveMode(
				activeMode.hasActiveMode(),
				activeMode.modeCode(),
				activeMode.modeName(),
				activeMode.scenarioId(),
				activeMode.guidance());
	}

	private DashboardSummaryResponse.FinancialSnapshot toFinancialSnapshot(
			ScenarioResponse scenario,
			ScenarioSummaryResponse summary,
			TransportEvaluationEntity latestTransport) {
		if (scenario == null || summary == null) {
			return new DashboardSummaryResponse.FinancialSnapshot(
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		}
		BigDecimal income = summary.monthlyIncome();
		BigDecimal expenses = summary.totalMonthlyExpenses();
		BigDecimal savings = summary.estimatedMonthlyAvailable();
		return new DashboardSummaryResponse.FinancialSnapshot(
				income,
				expenses,
				savings,
				ratio(savings, income),
				BigDecimal.ZERO,
				ratio(expenses, income),
				latestTransport == null ? BigDecimal.ZERO : latestTransport.getTransportBurden(),
				ratio(scenario.emergencyFundCurrent(), expenses));
	}

	private DashboardSummaryResponse.RiskSummary toMainRisk(RiskFactorEntity risk) {
		return new DashboardSummaryResponse.RiskSummary(risk.getSeverity(), risk.getTitle(), risk.getExplanation());
	}

	private DashboardSummaryResponse.RecommendationSummary toSummaryRecommendation(RecommendationEntity recommendation) {
		return new DashboardSummaryResponse.RecommendationSummary(
				recommendation.getId(),
				recommendation.getScenarioId(),
				recommendation.getSeverity(),
				recommendation.getTitle(),
				recommendation.getMessage(),
				recommendation.getActionLabel(),
				recommendation.getActionType());
	}

	private DashboardSummaryResponse.ScenarioComparisonSummary toComparisonSummary(int scenarioCount, List<DecisionEventEntity> recentEvents) {
		DecisionEventEntity comparison = recentEvents.stream()
				.filter(event -> "SCENARIO_COMPARED".equals(event.getDecisionType()))
				.findFirst()
				.orElse(null);
		return new DashboardSummaryResponse.ScenarioComparisonSummary(
				scenarioCount >= 2,
				scenarioCount,
				comparison == null ? null : comparison.getScenarioId(),
				comparison == null ? null : comparison.getReason());
	}

	private DashboardSummaryResponse.TransportSummary toTransportSummary(TransportEvaluationEntity latestTransport) {
		if (latestTransport == null) {
			return new DashboardSummaryResponse.TransportSummary(false, null, null, BigDecimal.ZERO, null, null, null, null);
		}
		return new DashboardSummaryResponse.TransportSummary(
				true,
				latestTransport.getRecommendedCurrentOption(),
				latestTransport.getFutureViableOption(),
				latestTransport.getTransportBurden(),
				latestTransport.getFitScore(),
				latestTransport.getRiskLevel(),
				latestTransport.getExplanation(),
				latestTransport.getConditionsToSwitch());
	}

	private DashboardSummaryResponse.HomePriority toHomePriority(HomeSetupSummaryResponse summary) {
		return new DashboardSummaryResponse.HomePriority(
				summary.totalItems() > 0,
				summary.nextBestPurchase() == null ? null : summary.nextBestPurchase().name(),
				summary.tier1CompletionPercentage(),
				summary.pendingItems());
	}

	private DashboardSummaryResponse.MealSuggestionSummary toMealSuggestion(ActiveModeSummaryResponse activeMode) {
		String title = activeMode.hasActiveMode() ? "Pick a meal that fits " + activeMode.modeName() : "Pick a low-effort meal";
		return new DashboardSummaryResponse.MealSuggestionSummary(title, "Open meals for suggestions based on energy, budget, craving, and mode.");
	}

	private DashboardSummaryResponse.DecisionEventSummary toDecisionEventSummary(DecisionEventEntity event) {
		return new DashboardSummaryResponse.DecisionEventSummary(
				event.getId(),
				event.getScenarioId(),
				event.getDecisionType(),
				event.getQuestion(),
				event.getChosenOption(),
				event.getReason(),
				event.getCreatedAt());
	}

	private List<DashboardSummaryResponse.QuickAction> quickActions(
			ScenarioResponse scenario,
			ScoreSnapshotEntity latestScore,
			List<RiskFactorEntity> risks,
			List<RecommendationEntity> recommendations,
			HomeSetupSummaryResponse homeSummary,
			TransportEvaluationEntity latestTransport) {
		if (scenario == null) {
			return List.of(new DashboardSummaryResponse.QuickAction("CREATE_SCENARIO", "Create first scenario", "/scenarios", "HIGH"));
		}
		if (latestScore == null) {
			return List.of(new DashboardSummaryResponse.QuickAction("CALCULATE_SCORE", "Calculate independence score", "/scenarios/" + scenario.id(), "HIGH"));
		}
		if (!risks.isEmpty()) {
			return List.of(new DashboardSummaryResponse.QuickAction("REVIEW_RISK", "Review main risk", "/scenarios/" + scenario.id(), "HIGH"));
		}
		if (!recommendations.isEmpty()) {
			return List.of(new DashboardSummaryResponse.QuickAction("OPEN_RECOMMENDATION", "Act on top recommendation", "/recommendations", "HIGH"));
		}
		if (latestTransport == null) {
			return List.of(new DashboardSummaryResponse.QuickAction("EVALUATE_TRANSPORT", "Evaluate transport options", "/scenarios/" + scenario.id(), "MEDIUM"));
		}
		if (homeSummary.totalItems() == 0) {
			return List.of(new DashboardSummaryResponse.QuickAction("INITIALIZE_HOME", "Initialize home setup", "/home-setup", "MEDIUM"));
		}
		return List.of(new DashboardSummaryResponse.QuickAction("CHECK_MEALS", "Plan an easy meal", "/meals", "LOW"));
	}

	private BigDecimal ratio(BigDecimal numerator, BigDecimal denominator) {
		if (denominator == null || denominator.signum() <= 0) {
			return BigDecimal.ZERO;
		}
		return (numerator == null ? BigDecimal.ZERO : numerator).divide(denominator, 6, RoundingMode.HALF_UP);
	}
}
