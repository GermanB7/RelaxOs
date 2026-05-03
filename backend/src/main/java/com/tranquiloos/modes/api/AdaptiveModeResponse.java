package com.tranquiloos.modes.api;

import com.tranquiloos.modes.domain.AlertPolicy;
import com.tranquiloos.modes.domain.IntensityLevel;
import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.modes.domain.PurchasePolicy;
import com.tranquiloos.modes.domain.RoutinePolicy;
import com.tranquiloos.modes.domain.SpendingPolicy;

public record AdaptiveModeResponse(
		Long id,
		ModeCode code,
		String name,
		String description,
		String objective,
		Integer recommendedMinDays,
		Integer recommendedMaxDays,
		IntensityLevel intensityLevel,
		SpendingPolicy spendingPolicy,
		AlertPolicy alertPolicy,
		PurchasePolicy purchasePolicy,
		RoutinePolicy routinePolicy,
		Integer sortOrder) {
}
