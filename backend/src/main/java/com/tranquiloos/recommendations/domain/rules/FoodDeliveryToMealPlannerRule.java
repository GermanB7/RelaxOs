package com.tranquiloos.recommendations.domain.rules;

import java.math.BigDecimal;
import java.util.Optional;

import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationType;

public class FoodDeliveryToMealPlannerRule implements RecommendationRule {

	@Override
	public Optional<RecommendationCandidate> evaluate(RecommendationContext context) {
		if (context.foodDeliveryBurden().compareTo(new BigDecimal("0.05")) <= 0
				&& !context.hasRisk("FOOD_DELIVERY_PRESSURE")) {
			return Optional.empty();
		}
		return Optional.of(new RecommendationCandidate(
				RecommendationType.FOOD_DELIVERY,
				RecommendationSeverity.MEDIUM,
				6,
				"Use meal planner before ordering delivery",
				"Delivery is becoming a recurring leak. Pick one low-effort fallback meal before ordering.",
				"Open meal planner",
				"OPEN_MEAL_PLANNER",
				"FOOD_DELIVERY_TO_MEAL_PLANNER_RULE"));
	}
}
