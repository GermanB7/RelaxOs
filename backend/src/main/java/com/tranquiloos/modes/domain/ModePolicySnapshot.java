package com.tranquiloos.modes.domain;

public record ModePolicySnapshot(
		Long activationId,
		ModeCode modeCode,
		String modeName,
		IntensityLevel intensityLevel,
		SpendingPolicy spendingPolicy,
		AlertPolicy alertPolicy,
		PurchasePolicy purchasePolicy,
		RoutinePolicy routinePolicy) {
}
