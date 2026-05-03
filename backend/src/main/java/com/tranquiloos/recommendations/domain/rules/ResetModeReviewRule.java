package com.tranquiloos.recommendations.domain.rules;

import java.util.Optional;

import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationType;

public class ResetModeReviewRule implements RecommendationRule {

	@Override
	public Optional<RecommendationCandidate> evaluate(RecommendationContext context) {
		if (!context.isMode(ModeCode.RESET_MODE)) {
			return Optional.empty();
		}
		return Optional.of(new RecommendationCandidate(
				RecommendationType.GENERAL,
				RecommendationSeverity.LOW,
				8,
				"Reset mode: clean up the system",
				"Reset mode is active. Review your scenario, pending purchases, and open recommendations before adding new plans.",
				"Open dashboard",
				"OPEN_DASHBOARD",
				"RESET_MODE_REVIEW_RULE"));
	}
}
