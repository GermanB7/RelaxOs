package com.tranquiloos.recommendations.domain.rules;

import java.math.BigDecimal;
import java.util.Optional;

import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationType;

public class WarModeSpendingFocusRule implements RecommendationRule {

	@Override
	public Optional<RecommendationCandidate> evaluate(RecommendationContext context) {
		if (!context.isMode(ModeCode.WAR_MODE)) {
			return Optional.empty();
		}
		boolean spendingPressure = context.foodDeliveryBurden().compareTo(new BigDecimal("0.05")) > 0
				|| context.savingsRate().compareTo(new BigDecimal("0.10")) < 0;
		if (!spendingPressure) {
			return Optional.empty();
		}
		return Optional.of(new RecommendationCandidate(
				RecommendationType.GENERAL,
				RecommendationSeverity.HIGH,
				2,
				"Modo Guerra: protect savings and focus",
				"Modo Guerra is active, but the scenario still has flexible spending pressure. Keep non-essential purchases frozen and push margin toward the priority goal.",
				"Review expenses",
				"OPEN_EXPENSES",
				"WAR_MODE_SPENDING_FOCUS_RULE"));
	}
}
