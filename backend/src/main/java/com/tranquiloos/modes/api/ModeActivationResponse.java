package com.tranquiloos.modes.api;

import java.time.Instant;

import com.tranquiloos.modes.domain.AlertPolicy;
import com.tranquiloos.modes.domain.IntensityLevel;
import com.tranquiloos.modes.domain.ModeActivationStatus;
import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.modes.domain.PurchasePolicy;
import com.tranquiloos.modes.domain.RoutinePolicy;
import com.tranquiloos.modes.domain.SpendingPolicy;

public record ModeActivationResponse(
		Long activationId,
		ModeCode modeCode,
		String modeName,
		Long scenarioId,
		String objective,
		IntensityLevel intensityLevel,
		SpendingPolicy spendingPolicy,
		AlertPolicy alertPolicy,
		PurchasePolicy purchasePolicy,
		RoutinePolicy routinePolicy,
		ModeActivationStatus status,
		Instant activatedAt,
		Instant expiresAt,
		Instant endedAt,
		String notes) {
}
