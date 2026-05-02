package com.tranquiloos.recommendations.domain;

public record RecommendationCandidate(
		RecommendationType type,
		RecommendationSeverity severity,
		int priority,
		String title,
		String message,
		String actionLabel,
		String actionType,
		String sourceRuleKey) {
}
