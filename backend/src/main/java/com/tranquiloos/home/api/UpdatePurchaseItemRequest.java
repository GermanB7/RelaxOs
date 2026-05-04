package com.tranquiloos.home.api;

import java.math.BigDecimal;

import com.tranquiloos.home.domain.PurchaseTier;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdatePurchaseItemRequest(
		@Size(max = 160) String name,
		@Size(max = 80) String category,
		PurchaseTier tier,
		@PositiveOrZero BigDecimal estimatedPrice,
		@PositiveOrZero BigDecimal actualPrice,
		@Min(1) @Max(100) Integer priority,
		@Size(max = 2000) String link,
		@Size(max = 2000) String notes) {
}
