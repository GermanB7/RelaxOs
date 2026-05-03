package com.tranquiloos.home.domain;

/**
 * Calculates purchase priority based on tier and adjustments for impact and urgency.
 * Priority scale: 1 (highest) to 100 (lowest).
 */
public class PurchasePriorityCalculator {

	/**
	 * Calculate priority for catalog items.
	 */
	public static Integer calculatePriority(PurchaseTier tier, ImpactLevel impact, UrgencyLevel urgency) {
		int basePriority = basePriority(tier);
		int urgencyAdjustment = urgencyAdjustment(urgency);
		int impactAdjustment = impactAdjustment(impact);

		int priority = basePriority + urgencyAdjustment + impactAdjustment;
		return Math.max(1, Math.min(100, priority));
	}

	/**
	 * Calculate default priority for custom items based on tier only.
	 */
	public static Integer calculateDefaultPriority(PurchaseTier tier) {
		return switch (tier) {
		case TIER_1 -> 20;
		case TIER_2 -> 40;
		case TIER_3 -> 70;
		case TIER_4 -> 90;
		};
	}

	private static int basePriority(PurchaseTier tier) {
		return switch (tier) {
		case TIER_1 -> 10;
		case TIER_2 -> 30;
		case TIER_3 -> 60;
		case TIER_4 -> 90;
		};
	}

	private static int urgencyAdjustment(UrgencyLevel urgency) {
		return switch (urgency) {
		case HIGH -> -5;
		case MEDIUM -> 0;
		case LOW -> 5;
		};
	}

	private static int impactAdjustment(ImpactLevel impact) {
		return switch (impact) {
		case HIGH -> -5;
		case MEDIUM -> 0;
		case LOW -> 5;
		};
	}
}
