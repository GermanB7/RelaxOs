package com.tranquiloos.recommendations.domain.rules;

import java.util.Optional;

import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationType;
import com.tranquiloos.recommendations.domain.RulePriority;
import com.tranquiloos.scoring.domain.ConfidenceLevel;

public class DataQualityRecommendationRule implements RecommendationRule {

	@Override
	public Optional<RecommendationCandidate> evaluate(RecommendationContext context) {
		if (context.confidenceLevel() != ConfidenceLevel.LOW && context.scenarioExpenses().size() >= 4) {
			return Optional.empty();
		}
		return Optional.of(new RecommendationCandidate(
				RecommendationType.DATA_QUALITY,
				RecommendationSeverity.LOW,
				RulePriority.DATA_QUALITY,
				"Add more expense data",
				"The score has low confidence because the scenario is missing expense details. Add rent, utilities, groceries, transport, and internet for better recommendations.",
				"Complete scenario expenses",
				"OPEN_EXPENSES",
				"DATA_QUALITY_RULE"));
	}
}
