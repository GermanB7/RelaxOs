package com.tranquiloos.recommendations.api;

import java.util.List;

public record RecalculateRecommendationsResponse(Long scenarioId, int generatedCount, List<RecommendationResponse> recommendations) {
}
