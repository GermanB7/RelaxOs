package com.tranquiloos.home.api;

import java.math.BigDecimal;

import com.tranquiloos.home.domain.PurchaseTier;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateCustomPurchaseItemRequest(
		Long scenarioId,
		@NotBlank @Size(max = 160) String name,
		@NotBlank @Size(max = 80) String category,
		@NotNull PurchaseTier tier,
		@PositiveOrZero BigDecimal estimatedPrice,
		@Min(1) @Max(100) Integer priority,
		@Size(max = 2000) String link,
		@Size(max = 2000) String notes) {
}
