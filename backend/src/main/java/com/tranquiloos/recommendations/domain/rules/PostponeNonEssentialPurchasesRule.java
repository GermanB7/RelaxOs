package com.tranquiloos.recommendations.domain.rules;

import java.math.BigDecimal;
import java.util.Optional;

import com.tranquiloos.home.infrastructure.UserPurchaseItemJpaRepository;
import com.tranquiloos.recommendations.domain.RecommendationCandidate;
import com.tranquiloos.recommendations.domain.RecommendationContext;
import com.tranquiloos.recommendations.domain.RecommendationRule;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationType;

/**
 * Rule: Postpone Non-Essential Purchases
 * Triggers when score is low or emergency fund is critical, and user has non-essential pending items.
 */
public class PostponeNonEssentialPurchasesRule implements RecommendationRule {

	private final UserPurchaseItemJpaRepository userPurchaseRepository;

	public PostponeNonEssentialPurchasesRule(UserPurchaseItemJpaRepository userPurchaseRepository) {
		this.userPurchaseRepository = userPurchaseRepository;
	}

	@Override
	public Optional<RecommendationCandidate> evaluate(RecommendationContext context) {
		boolean scoreLow = context.score() < 61;
		boolean emergencyFundCritical = context.hasRisk("LOW_EMERGENCY_FUND")
				&& context.emergencyCoverageMonths().compareTo(BigDecimal.ONE) < 0;

		if (!scoreLow && !emergencyFundCritical) {
			return Optional.empty();
		}

		// Check for non-essential pending items (TIER_2, TIER_3, TIER_4)
		Long nonEssentialPendingCount = userPurchaseRepository.countByUserIdAndTierAndStatus(context.userId(),
				"TIER_2", "PENDING");
		if (nonEssentialPendingCount == 0) {
			nonEssentialPendingCount = userPurchaseRepository.countByUserIdAndTierAndStatus(context.userId(), "TIER_3",
					"PENDING");
		}
		if (nonEssentialPendingCount == 0) {
			nonEssentialPendingCount = userPurchaseRepository.countByUserIdAndTierAndStatus(context.userId(), "TIER_4",
					"PENDING");
		}

		// Also check wishlist items
		if (nonEssentialPendingCount == 0) {
			nonEssentialPendingCount = userPurchaseRepository.countByUserIdAndTierAndStatus(context.userId(),
					"TIER_2", "WISHLIST");
		}
		if (nonEssentialPendingCount == 0) {
			nonEssentialPendingCount = userPurchaseRepository.countByUserIdAndTierAndStatus(context.userId(),
					"TIER_3", "WISHLIST");
		}
		if (nonEssentialPendingCount == 0) {
			nonEssentialPendingCount = userPurchaseRepository.countByUserIdAndTierAndStatus(context.userId(),
					"TIER_4", "WISHLIST");
		}

		if (nonEssentialPendingCount == 0) {
			return Optional.empty();
		}

		return Optional.of(new RecommendationCandidate(
				RecommendationType.HOUSEHOLD_SETUP,
				RecommendationSeverity.HIGH,
				8,
				"Postpone non-essential purchases",
				"Good purchase, bad moment. Keep comfort and smart-home items in wishlist until the emergency fund and monthly margin are safer.",
				"Review wishlist",
				"OPEN_HOME_ROADMAP",
				"POSTPONE_NON_ESSENTIAL_PURCHASES_RULE"));
	}
}
