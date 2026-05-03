package com.tranquiloos.recommendations.domain.rules;

import java.util.Optional;

import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationType;

public class WarModeLowCostMealsRule implements RecommendationRule {

	@Override
	public Optional<RecommendationCandidate> evaluate(RecommendationContext context) {
		if (!context.isMode(ModeCode.WAR_MODE) && !context.isMode(ModeCode.AGGRESSIVE_SAVING_MODE)) {
			return Optional.empty();
		}
		return Optional.of(new RecommendationCandidate(
				RecommendationType.FOOD_DELIVERY,
				RecommendationSeverity.MEDIUM,
				6,
				"Choose low-cost repeatable meals",
				"Your active mode rewards simple repeatable meals. Use cheap low-effort options before spending on delivery.",
				"Find low-cost meals",
				"OPEN_MEAL_PLANNER",
				"WAR_MODE_LOW_COST_MEALS_RULE"));
	}
}
