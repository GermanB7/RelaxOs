package com.tranquiloos.dashboard.api;

public record DashboardHomeSetupResponse(
		boolean hasRoadmap,
		Integer tier1CompletionPercentage,
		String nextBestPurchaseName,
		Long pendingItems) {
}
