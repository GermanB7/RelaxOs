package com.tranquiloos.home.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PurchasePriorityCalculatorTest {

	@Test
	public void testTier1HighImpactHighUrgency() {
		Integer priority = PurchasePriorityCalculator.calculatePriority(
				PurchaseTier.TIER_1,
				ImpactLevel.HIGH,
				UrgencyLevel.HIGH);

		// Base: 10, Impact HIGH: -5, Urgency HIGH: -5
		// Expected: 10 - 5 - 5 = 0, clamped to 1
		assertEquals(1, priority);
	}

	@Test
	public void testTier4LowImpactLowUrgency() {
		Integer priority = PurchasePriorityCalculator.calculatePriority(
				PurchaseTier.TIER_4,
				ImpactLevel.LOW,
				UrgencyLevel.LOW);

		// Base: 90, Impact LOW: +5, Urgency LOW: +5
		// Expected: 90 + 5 + 5 = 100
		assertEquals(100, priority);
	}

	@Test
	public void testTier2MediumImpactMediumUrgency() {
		Integer priority = PurchasePriorityCalculator.calculatePriority(
				PurchaseTier.TIER_2,
				ImpactLevel.MEDIUM,
				UrgencyLevel.MEDIUM);

		// Base: 30, Impact MEDIUM: 0, Urgency MEDIUM: 0
		// Expected: 30
		assertEquals(30, priority);
	}

	@Test
	public void testPriorityClamping() {
		// Test minimum clamp
		Integer minPriority = PurchasePriorityCalculator.calculatePriority(
				PurchaseTier.TIER_1,
				ImpactLevel.HIGH,
				UrgencyLevel.HIGH);
		assertTrue(minPriority >= 1);

		// Test maximum clamp
		Integer maxPriority = PurchasePriorityCalculator.calculatePriority(
				PurchaseTier.TIER_4,
				ImpactLevel.LOW,
				UrgencyLevel.LOW);
		assertTrue(maxPriority <= 100);
	}

	@Test
	public void testDefaultPriorityForCustomItems() {
		assertEquals(20, PurchasePriorityCalculator.calculateDefaultPriority(PurchaseTier.TIER_1));
		assertEquals(40, PurchasePriorityCalculator.calculateDefaultPriority(PurchaseTier.TIER_2));
		assertEquals(70, PurchasePriorityCalculator.calculateDefaultPriority(PurchaseTier.TIER_3));
		assertEquals(90, PurchasePriorityCalculator.calculateDefaultPriority(PurchaseTier.TIER_4));
	}
}
