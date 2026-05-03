package com.tranquiloos.dashboard.api;

import com.tranquiloos.recommendations.domain.RecommendationSeverity;

public record DashboardRecommendationResponse(
		Long id,
		Long scenarioId,
		RecommendationSeverity severity,
		String title,
		String actionLabel,
		String actionType) {
}
