package com.tranquiloos.recommendations.domain.rules;

import java.util.Optional;

import com.tranquiloos.home.infrastructure.UserPurchaseItemJpaRepository;
import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationType;

/**
 * Rule: Buy Tier 1 Home Setup items first
 * Triggers when user has pending TIER_1 items and score is healthy.
 */
public class BuyTier1HomeSetupRule implements RecommendationRule {

	private final UserPurchaseItemJpaRepository userPurchaseRepository;

	public BuyTier1HomeSetupRule(UserPurchaseItemJpaRepository userPurchaseRepository) {
		this.userPurchaseRepository = userPurchaseRepository;
	}

	@Override
	public Optional<RecommendationCandidate> evaluate(RecommendationContext context) {
		// Only active if roadmap initialized (has any TIER_1 items)
		Long tier1Count = userPurchaseRepository.countByUserIdAndTierAndStatusNot(context.userId(), "TIER_1",
				"SKIPPED");
		if (tier1Count == 0) {
			return Optional.empty();
		}

		// Only if there are pending TIER_1 items
		Long tier1PendingCount = userPurchaseRepository.countByUserIdAndTierAndStatus(context.userId(), "TIER_1",
				"PENDING");
		if (tier1PendingCount == 0) {
			return Optional.empty();
		}

		// Only recommend if score is healthy (>= 41)
		if (context.score() < 41) {
			return Optional.empty();
		}

		// Check for emergency fund crisis - don't recommend if critical
		if (context.hasRisk("LOW_EMERGENCY_FUND") && context.emergencyCoverageMonths().signum() <= 0) {
			return Optional.empty();
		}

		return Optional.of(new RecommendationCandidate(
				RecommendationType.HOUSEHOLD_SETUP,
				RecommendationSeverity.MEDIUM,
				7,
				"Buy Tier 1 home essentials first",
				"Your home setup still has essential pending items. Prioritize functional basics before comfort, decoration, or smart-home purchases.",
				"Open home roadmap",
				"OPEN_HOME_ROADMAP",
				"BUY_TIER_1_HOME_SETUP_RULE"));
	}
}
