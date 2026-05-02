package com.tranquiloos.expenses.api;

import java.math.BigDecimal;

import com.tranquiloos.expenses.domain.ExpenseFrequency;

public record ScenarioExpenseResponse(
		Long id,
		Long scenarioId,
		Long categoryId,
		String categoryCode,
		String categoryName,
		String name,
		BigDecimal amount,
		ExpenseFrequency frequency,
		boolean isEssential,
		BigDecimal monthlyEquivalent) {
}
