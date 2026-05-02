package com.tranquiloos.recommendations.api;

import java.time.Instant;

import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationStatus;
import com.tranquiloos.recommendations.domain.RecommendationType;

public record RecommendationResponse(
		Long id,
		Long userId,
		Long scenarioId,
		Long scoreSnapshotId,
		RecommendationType type,
		RecommendationSeverity severity,
		int priority,
		String title,
		String message,
		String actionLabel,
		String actionType,
		String sourceRuleKey,
		RecommendationStatus status,
		Instant createdAt,
		Instant updatedAt) {
}
