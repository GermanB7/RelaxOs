package com.tranquiloos.scenarios.api;

import java.math.BigDecimal;

public record ScenarioSummaryResponse(
		Long scenarioId,
		BigDecimal monthlyIncome,
		BigDecimal totalMonthlyExpenses,
		BigDecimal estimatedMonthlyAvailable,
		long expenseCount) {
}
