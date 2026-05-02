package com.tranquiloos.recommendations.domain.rules;

import java.math.BigDecimal;
import java.util.Optional;

import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationType;
import com.tranquiloos.recommendations.domain.RulePriority;

public class LowEmergencyFundRecommendationRule implements RecommendationRule {

	@Override
	public Optional<RecommendationCandidate> evaluate(RecommendationContext context) {
		if (!context.hasRisk("LOW_EMERGENCY_FUND") && context.emergencyCoverageMonths().compareTo(BigDecimal.ONE) >= 0) {
			return Optional.empty();
		}
		RecommendationSeverity severity = context.emergencyCoverageMonths().signum() <= 0
				? RecommendationSeverity.CRITICAL
				: RecommendationSeverity.HIGH;
		return Optional.of(new RecommendationCandidate(
				RecommendationType.EMERGENCY_FUND,
				severity,
				RulePriority.LOW_EMERGENCY_FUND,
				"Build emergency fund first",
				"Your emergency fund covers less than one month of expenses. Prioritize liquidity before vehicle, luxury, or smart-home purchases.",
				"Increase emergency fund",
				"EDIT_SCENARIO",
				"LOW_EMERGENCY_FUND_RULE"));
	}
}
