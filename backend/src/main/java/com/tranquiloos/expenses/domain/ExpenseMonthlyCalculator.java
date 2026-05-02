package com.tranquiloos.expenses.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExpenseMonthlyCalculator {

	public BigDecimal monthlyEquivalent(BigDecimal amount, ExpenseFrequency frequency) {
		if (amount == null || frequency == null) {
			return BigDecimal.ZERO;
		}

		return switch (frequency) {
			case MONTHLY -> amount;
			case WEEKLY -> amount.multiply(BigDecimal.valueOf(4));
			case YEARLY -> amount.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
			case ONE_TIME -> BigDecimal.ZERO;
		};
	}
}
