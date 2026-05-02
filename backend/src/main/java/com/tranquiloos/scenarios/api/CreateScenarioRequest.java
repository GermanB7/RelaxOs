package com.tranquiloos.scenarios.api;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateScenarioRequest(
		@NotBlank @Size(max = 160) String name,
		@NotNull @PositiveOrZero BigDecimal monthlyIncome,
		@PositiveOrZero BigDecimal emergencyFundCurrent,
		@PositiveOrZero BigDecimal emergencyFundTarget) {
}
