package com.tranquiloos.home.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserPurchaseItemResponse(
		Long id,
		String name,
		String category,
		String tier,
		BigDecimal estimatedPrice,
		BigDecimal actualPrice,
		String status,
		Integer priority,
		String link,
		String notes,
		LocalDateTime purchasedAt,
		LocalDate postponedUntil,
		LocalDateTime createdAt,
		LocalDateTime updatedAt) {
}
