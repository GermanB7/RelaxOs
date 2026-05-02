package com.tranquiloos.scoring.domain;

import java.math.BigDecimal;
import java.util.List;

public record ScoreInput(
		Long scenarioId,
		BigDecimal monthlyIncome,
		BigDecimal emergencyFundCurrent,
		BigDecimal monthlyExpenses,
		BigDecimal rentMonthly,
		BigDecimal essentialMonthlyExpenses,
		BigDecimal debtMonthly,
		BigDecimal foodDeliveryMonthly,
		int expenseCount,
		List<String> categoryCodes) {
}
