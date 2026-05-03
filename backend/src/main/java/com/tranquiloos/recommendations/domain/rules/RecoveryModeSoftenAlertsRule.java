package com.tranquiloos.recommendations.domain.rules;

import java.util.Optional;

import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationType;

public class RecoveryModeSoftenAlertsRule implements RecommendationRule {

	@Override
	public Optional<RecommendationCandidate> evaluate(RecommendationContext context) {
		if (!context.isMode(ModeCode.RECOVERY_MODE) || context.highOpenRecommendationCount() <= 0) {
			return Optional.empty();
		}
		return Optional.of(new RecommendationCandidate(
				RecommendationType.GENERAL,
				RecommendationSeverity.MEDIUM,
				9,
				"Modo Recuperación: keep only minimum viable actions",
				"Recovery mode is active. Keep financial risks visible, but avoid overloading yourself. Focus on one minimum viable action today.",
				"View top recommendation",
				"OPEN_RECOMMENDATIONS",
				"RECOVERY_MODE_SOFTEN_ALERTS_RULE"));
	}
}
