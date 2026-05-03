package com.tranquiloos.dashboard.application;

import java.util.Comparator;
import java.util.List;

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
import com.tranquiloos.scenarios.api.ScenarioResponse;
import com.tranquiloos.scenarios.api.ScenarioSummaryResponse;
import com.tranquiloos.scenarios.application.ScenarioService;
import com.tranquiloos.scoring.domain.RiskSeverity;
import com.tranquiloos.scoring.infrastructure.RiskFactorEntity;
import com.tranquiloos.scoring.infrastructure.RiskFactorRepository;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotEntity;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotRepository;
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

	public DashboardService(
			CurrentUserProvider currentUserProvider,
			ProfileService profileService,
			ScenarioService scenarioService,
			ScoreSnapshotRepository scoreSnapshotRepository,
			RiskFactorRepository riskFactorRepository,
			RecommendationJpaRepository recommendationRepository,
			ActiveModeProvider activeModeProvider,
			HomeSetupService homeSetupService) {
		this.currentUserProvider = currentUserProvider;
		this.profileService = profileService;
		this.scenarioService = scenarioService;
		this.scoreSnapshotRepository = scoreSnapshotRepository;
		this.riskFactorRepository = riskFactorRepository;
		this.recommendationRepository = recommendationRepository;
		this.activeModeProvider = activeModeProvider;
		this.homeSetupService = homeSetupService;
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
}
