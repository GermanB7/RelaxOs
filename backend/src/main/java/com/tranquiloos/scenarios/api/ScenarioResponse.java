package com.tranquiloos.scenarios.api;

import java.math.BigDecimal;
import java.time.Instant;

import com.tranquiloos.scenarios.domain.ScenarioStatus;

public record ScenarioResponse(
		Long id,
		String name,
		BigDecimal monthlyIncome,
		BigDecimal emergencyFundCurrent,
		BigDecimal emergencyFundTarget,
		ScenarioStatus status,
		Instant createdAt,
		Instant updatedAt) {
}
