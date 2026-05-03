package com.tranquiloos.modes.api;

import java.time.Instant;
import java.util.List;

import com.tranquiloos.modes.domain.AlertPolicy;
import com.tranquiloos.modes.domain.IntensityLevel;
import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.modes.domain.PurchasePolicy;
import com.tranquiloos.modes.domain.RoutinePolicy;
import com.tranquiloos.modes.domain.SpendingPolicy;

public record ActiveModeSummaryResponse(
		boolean hasActiveMode,
		Long activationId,
		ModeCode modeCode,
		String modeName,
		String objective,
		IntensityLevel intensityLevel,
		SpendingPolicy spendingPolicy,
		AlertPolicy alertPolicy,
		PurchasePolicy purchasePolicy,
		RoutinePolicy routinePolicy,
		Long scenarioId,
		Instant activatedAt,
		Instant expiresAt,
		Long daysRemaining,
		List<String> guidance) {

	public static ActiveModeSummaryResponse empty() {
		return new ActiveModeSummaryResponse(false, null, null, null, null, null, null, null, null, null, null, null, null, null, List.of());
	}
}
