package com.tranquiloos.recommendations.domain.rules;

import java.util.Optional;

import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationType;
import com.tranquiloos.recommendations.domain.RulePriority;

public class NegativeMarginRecommendationRule implements RecommendationRule {

	@Override
	public Optional<RecommendationCandidate> evaluate(RecommendationContext context) {
		if (context.estimatedMonthlyAvailable().signum() >= 0) {
			return Optional.empty();
		}
		return Optional.of(new RecommendationCandidate(
				RecommendationType.MONTHLY_MARGIN,
				RecommendationSeverity.CRITICAL,
				RulePriority.NEGATIVE_MARGIN,
				"Monthly margin is negative",
				"This scenario spends more than it earns monthly. Fix rent, fixed expenses, or income before treating it as viable.",
				"Adjust scenario expenses",
				"OPEN_SCENARIO",
				"NEGATIVE_MARGIN_RULE"));
	}
}
