package com.tranquiloos.transport.api;

import java.math.BigDecimal;

import com.tranquiloos.transport.domain.TransportOptionType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record TransportOptionRequest(
		@NotNull TransportOptionType optionType,
		@NotNull @PositiveOrZero BigDecimal monthlyCost,
		@NotNull @Min(0) Integer tripsPerWeek,
		@NotNull @Min(0) Integer averageTimeMinutes,
		@NotNull @Min(1) @Max(5) Integer comfortScore,
		@NotNull @Min(1) @Max(5) Integer safetyScore,
		@NotNull @Min(1) @Max(5) Integer flexibilityScore,
		@PositiveOrZero BigDecimal parkingCost,
		@PositiveOrZero BigDecimal maintenanceCost,
		@PositiveOrZero BigDecimal insuranceCost,
		@PositiveOrZero BigDecimal fuelCost,
		@PositiveOrZero BigDecimal upfrontCost,
		Boolean hasParking,
		Boolean hasLicense,
		@Size(max = 2000) String notes) {
}
