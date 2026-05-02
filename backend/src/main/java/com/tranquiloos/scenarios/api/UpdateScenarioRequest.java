package com.tranquiloos.scenarios.api;

import java.math.BigDecimal;

import com.tranquiloos.scenarios.domain.ScenarioStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateScenarioRequest(
		@NotBlank @Size(max = 160) String name,
		@NotNull @PositiveOrZero BigDecimal monthlyIncome,
		@PositiveOrZero BigDecimal emergencyFundCurrent,
		@PositiveOrZero BigDecimal emergencyFundTarget,
		@NotNull ScenarioStatus status) {
}
