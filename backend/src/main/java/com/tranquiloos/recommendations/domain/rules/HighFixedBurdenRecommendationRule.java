package com.tranquiloos.recommendations.domain.rules;

import java.math.BigDecimal;
import java.util.Optional;

import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationType;
import com.tranquiloos.recommendations.domain.RulePriority;

public class HighFixedBurdenRecommendationRule implements RecommendationRule {

	@Override
	public Optional<RecommendationCandidate> evaluate(RecommendationContext context) {
		if (!context.hasRisk("HIGH_FIXED_BURDEN") && context.fixedBurden().compareTo(new BigDecimal("0.60")) <= 0) {
			return Optional.empty();
		}
		RecommendationSeverity severity = context.fixedBurden().compareTo(new BigDecimal("0.75")) > 0
				? RecommendationSeverity.CRITICAL
				: RecommendationSeverity.HIGH;
		return Optional.of(new RecommendationCandidate(
				RecommendationType.FINANCIAL_RISK,
				severity,
				RulePriority.HIGH_FIXED_BURDEN,
				"Fixed expenses are too rigid",
				"Too much of your income is locked into fixed expenses. Reduce rigid monthly commitments before adding new obligations.",
				"Review fixed expenses",
				"OPEN_EXPENSES",
				"HIGH_FIXED_BURDEN_RULE"));
	}
}
