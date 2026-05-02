package com.tranquiloos.recommendations.domain.rules;

import java.math.BigDecimal;
import java.util.Optional;

import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationType;
import com.tranquiloos.recommendations.domain.RulePriority;

public class HighDebtBurdenRecommendationRule implements RecommendationRule {

	@Override
	public Optional<RecommendationCandidate> evaluate(RecommendationContext context) {
		if (!context.hasRisk("HIGH_DEBT_BURDEN") && context.debtBurden().compareTo(new BigDecimal("0.15")) <= 0) {
			return Optional.empty();
		}
		RecommendationSeverity severity = context.debtBurden().compareTo(new BigDecimal("0.25")) > 0
				? RecommendationSeverity.CRITICAL
				: RecommendationSeverity.HIGH;
		return Optional.of(new RecommendationCandidate(
				RecommendationType.DEBT,
				severity,
				RulePriority.HIGH_DEBT_BURDEN,
				"Debt is limiting independence",
				"Debt is competing with rent, food, and emergency fund. Freeze non-essential purchases and focus on lowering debt pressure.",
				"Review debt expenses",
				"OPEN_EXPENSES",
				"HIGH_DEBT_BURDEN_RULE"));
	}
}
