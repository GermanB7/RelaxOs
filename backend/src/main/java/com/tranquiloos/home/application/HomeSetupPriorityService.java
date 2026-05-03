package com.tranquiloos.home.application;

import com.tranquiloos.home.domain.ImpactLevel;
import com.tranquiloos.home.domain.PurchasePriorityCalculator;
import com.tranquiloos.home.domain.PurchaseTier;
import com.tranquiloos.home.domain.UrgencyLevel;
import org.springframework.stereotype.Service;

@Service
public class HomeSetupPriorityService {

	/**
	 * Calculate priority for a catalog item based on tier, impact, and urgency.
	 */
	public Integer calculatePriority(String tier, String impact, String urgency) {
		try {
			PurchaseTier purchaseTier = PurchaseTier.valueOf(tier);
			ImpactLevel impactLevel = ImpactLevel.valueOf(impact);
			UrgencyLevel urgencyLevel = UrgencyLevel.valueOf(urgency);
			return PurchasePriorityCalculator.calculatePriority(purchaseTier, impactLevel, urgencyLevel);
		} catch (IllegalArgumentException e) {
			// Default if conversion fails
			return 50;
		}
	}

	/**
	 * Calculate default priority for custom items based on tier only.
	 */
	public Integer calculateDefaultPriority(String tier) {
		try {
			PurchaseTier purchaseTier = PurchaseTier.valueOf(tier);
			return PurchasePriorityCalculator.calculateDefaultPriority(purchaseTier);
		} catch (IllegalArgumentException e) {
			// Default if conversion fails
			return 50;
		}
	}
}
