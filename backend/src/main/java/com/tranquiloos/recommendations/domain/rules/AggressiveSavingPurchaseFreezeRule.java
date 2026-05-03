package com.tranquiloos.recommendations.domain.rules;

import java.util.Optional;

import com.tranquiloos.home.infrastructure.UserPurchaseItemJpaRepository;
import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationType;

public class AggressiveSavingPurchaseFreezeRule implements RecommendationRule {

	private final UserPurchaseItemJpaRepository userPurchaseRepository;

	public AggressiveSavingPurchaseFreezeRule(UserPurchaseItemJpaRepository userPurchaseRepository) {
		this.userPurchaseRepository = userPurchaseRepository;
	}

	@Override
	public Optional<RecommendationCandidate> evaluate(RecommendationContext context) {
		if (!context.isMode(ModeCode.AGGRESSIVE_SAVING_MODE) || !hasNonEssentialPendingOrWishlist(context.userId())) {
			return Optional.empty();
		}
		return Optional.of(new RecommendationCandidate(
				RecommendationType.HOUSEHOLD_SETUP,
				RecommendationSeverity.HIGH,
				5,
				"Ahorro Agresivo: freeze non-essential purchases",
				"Aggressive saving mode is active. Keep Tier 2, Tier 3, and Tier 4 purchases in wishlist until the saving target is safer.",
				"Review home roadmap",
				"OPEN_HOME_ROADMAP",
				"AGGRESSIVE_SAVING_PURCHASE_FREEZE_RULE"));
	}

	private boolean hasNonEssentialPendingOrWishlist(Long userId) {
		for (String tier : java.util.List.of("TIER_2", "TIER_3", "TIER_4")) {
			if (userPurchaseRepository.countByUserIdAndTierAndStatus(userId, tier, "PENDING") > 0
					|| userPurchaseRepository.countByUserIdAndTierAndStatus(userId, tier, "WISHLIST") > 0) {
				return true;
			}
		}
		return false;
	}
}
