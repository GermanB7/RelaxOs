package com.tranquiloos.transport.api;

import java.math.BigDecimal;
import java.time.Instant;

import com.tranquiloos.transport.domain.TransportOptionType;

public record TransportOptionResponse(
		Long id,
		Long scenarioId,
		TransportOptionType optionType,
		BigDecimal monthlyCost,
		BigDecimal totalMonthlyCost,
		Integer tripsPerWeek,
		Integer averageTimeMinutes,
		Integer comfortScore,
		Integer safetyScore,
		Integer flexibilityScore,
		BigDecimal parkingCost,
		BigDecimal maintenanceCost,
		BigDecimal insuranceCost,
		BigDecimal fuelCost,
		BigDecimal upfrontCost,
		Boolean hasParking,
		Boolean hasLicense,
		String notes,
		Instant createdAt,
		Instant updatedAt) {
}
