package com.tranquiloos.home.api;

import java.math.BigDecimal;

import com.tranquiloos.home.domain.PurchaseStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdatePurchaseStatusRequest(
		@NotNull PurchaseStatus status,
		@PositiveOrZero BigDecimal actualPrice,
		@Size(max = 500) String reason) {
}
