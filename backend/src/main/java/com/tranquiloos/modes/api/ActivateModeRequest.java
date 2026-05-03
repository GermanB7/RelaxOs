package com.tranquiloos.modes.api;

import com.tranquiloos.modes.domain.IntensityLevel;
import com.tranquiloos.modes.domain.ModeCode;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActivateModeRequest(
		@NotNull ModeCode modeCode,
		Long scenarioId,
		@Size(max = 180) String objective,
		@Min(1) @Max(90) Integer durationDays,
		IntensityLevel intensityLevel,
		String notes) {
}
