package com.tranquiloos.recommendations.domain.rules;

import java.math.BigDecimal;
import java.util.Optional;

import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationType;
import com.tranquiloos.recommendations.domain.RulePriority;

public class HighRentBurdenRecommendationRule implements RecommendationRule {

	@Override
	public Optional<RecommendationCandidate> evaluate(RecommendationContext context) {
		if (!context.hasRisk("HIGH_RENT_BURDEN") && context.rentBurden().compareTo(new BigDecimal("0.35")) <= 0) {
			return Optional.empty();
		}
		RecommendationSeverity severity = context.rentBurden().compareTo(new BigDecimal("0.45")) > 0
				? RecommendationSeverity.CRITICAL
				: context.rentBurden().compareTo(new BigDecimal("0.40")) > 0 ? RecommendationSeverity.HIGH : RecommendationSeverity.MEDIUM;
		return Optional.of(new RecommendationCandidate(
				RecommendationType.RENT,
				severity,
				RulePriority.HIGH_RENT_BURDEN,
				"Rent is pressuring the scenario",
				"Rent is taking too much of your monthly income. Compare a cheaper place, roomie option, or waiting period before committing.",
				"Review rent assumption",
				"EDIT_SCENARIO",
				"HIGH_RENT_BURDEN_RULE"));
	}
}
