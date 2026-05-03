package com.tranquiloos.recommendations.domain.rules;

import java.math.BigDecimal;
import java.util.Optional;

import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationType;

public class LiveLifeBoundariesRule implements RecommendationRule {

	@Override
	public Optional<RecommendationCandidate> evaluate(RecommendationContext context) {
		if (!context.isMode(ModeCode.LIVE_LIFE_MODE)) {
			return Optional.empty();
		}
		boolean needsBoundaries = context.savingsRate().compareTo(new BigDecimal("0.05")) < 0
				|| context.emergencyCoverageMonths().compareTo(BigDecimal.ONE) < 0;
		if (!needsBoundaries) {
			return Optional.empty();
		}
		return Optional.of(new RecommendationCandidate(
				RecommendationType.FINANCIAL_RISK,
				RecommendationSeverity.MEDIUM,
				4,
				"Vivir la Vida needs boundaries",
				"Flexible mode is okay, but not if it touches emergency fund or minimum savings. Keep leisure inside a defined limit.",
				"Review scenario",
				"OPEN_SCENARIO",
				"LIVE_LIFE_BOUNDARIES_RULE"));
	}
}
