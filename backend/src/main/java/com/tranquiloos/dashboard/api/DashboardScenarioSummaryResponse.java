package com.tranquiloos.dashboard.api;

import java.math.BigDecimal;

public record DashboardScenarioSummaryResponse(
		Long id,
		String name,
		BigDecimal monthlyIncome,
		BigDecimal totalMonthlyExpenses,
		BigDecimal estimatedMonthlyAvailable) {
}
