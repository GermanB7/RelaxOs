package com.tranquiloos.home.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseCatalogItemResponse(
		Long id,
		String code,
		String name,
		String category,
		String tier,
		BigDecimal estimatedMinPrice,
		BigDecimal estimatedMaxPrice,
		String impactLevel,
		String urgencyLevel,
		String recommendedMoment,
		String earlyPurchaseRisk,
		String dependencies,
		String rationale,
		Boolean isActive,
		Integer sortOrder,
		LocalDateTime createdAt,
		LocalDateTime updatedAt) {
}
