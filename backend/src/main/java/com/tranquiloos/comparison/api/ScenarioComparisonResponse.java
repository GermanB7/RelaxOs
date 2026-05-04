package com.tranquiloos.comparison.api;

import java.util.List;

public record ScenarioComparisonResponse(
		List<ScenarioComparisonItemResponse> comparedScenarios,
		Long recommendedScenarioId,
		String recommendationReason) {
}
