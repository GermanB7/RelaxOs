package com.tranquiloos.users.api;

import java.math.BigDecimal;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
		@Size(max = 120) String displayName,
		@Size(max = 120) String city,
		@Size(max = 10) String currency,
		@PositiveOrZero BigDecimal monthlyIncome) {
}
