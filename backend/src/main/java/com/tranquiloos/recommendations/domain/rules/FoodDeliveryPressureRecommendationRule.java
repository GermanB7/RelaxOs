package com.tranquiloos.recommendations.domain.rules;

import java.math.BigDecimal;
import java.util.Optional;

import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationType;
import com.tranquiloos.recommendations.domain.RulePriority;

public class FoodDeliveryPressureRecommendationRule implements RecommendationRule {

	@Override
	public Optional<RecommendationCandidate> evaluate(RecommendationContext context) {
		if (!context.hasRisk("FOOD_DELIVERY_PRESSURE") && context.foodDeliveryBurden().compareTo(new BigDecimal("0.05")) <= 0) {
			return Optional.empty();
		}
		RecommendationSeverity severity = context.foodDeliveryBurden().compareTo(new BigDecimal("0.10")) > 0
				? RecommendationSeverity.MEDIUM
				: RecommendationSeverity.LOW;
		return Optional.of(new RecommendationCandidate(
				RecommendationType.FOOD_DELIVERY,
				severity,
				RulePriority.FOOD_DELIVERY_PRESSURE,
				"Food delivery is becoming a leak",
				"Delivery spending is high enough to justify a simple cooking fallback. Start with easy repeatable meals before optimizing nutrition.",
				"Plan cooking fallback",
				"OPEN_MEALS_FUTURE",
				"FOOD_DELIVERY_PRESSURE_RULE"));
	}
}
