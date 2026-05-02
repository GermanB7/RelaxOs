package com.tranquiloos.recommendations.domain;

import java.util.Optional;

public interface RecommendationRule {

	Optional<RecommendationCandidate> evaluate(RecommendationContext context);
}
