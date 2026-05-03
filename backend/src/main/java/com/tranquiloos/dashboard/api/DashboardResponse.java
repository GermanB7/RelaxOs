package com.tranquiloos.dashboard.api;

import java.util.List;

public record DashboardResponse(
		DashboardProfileResponse profile,
		DashboardScenarioSummaryResponse primaryScenario,
		DashboardScoreResponse latestScore,
		List<DashboardRiskResponse> topRisks,
		List<DashboardRecommendationResponse> topRecommendations,
		DashboardActiveModeResponse activeMode,
		DashboardHomeSetupResponse homeSetup,
		DashboardMealPlannerResponse mealPlanner) {
}
