package com.tranquiloos.home.api;

import java.math.BigDecimal;

public record HomeSetupSummaryResponse(
		Long totalItems,
		Long pendingItems,
		Long boughtItems,
		Long postponedItems,
		Long wishlistItems,
		Long tier1Total,
		Long tier1Bought,
		Integer tier1CompletionPercentage,
		BigDecimal estimatedPendingCost,
		NextBestPurchaseResponse nextBestPurchase) {

	public record NextBestPurchaseResponse(
			Long id,
			String name,
			String tier,
			String category,
			Integer priority) {
	}
}
