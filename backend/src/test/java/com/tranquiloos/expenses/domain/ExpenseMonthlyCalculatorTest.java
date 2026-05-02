package com.tranquiloos.expenses.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ExpenseMonthlyCalculatorTest {

	private final ExpenseMonthlyCalculator calculator = new ExpenseMonthlyCalculator();

	@Test
	void calculatesMonthlyExpenseAsSameAmount() {
		assertThat(calculator.monthlyEquivalent(new BigDecimal("100.00"), ExpenseFrequency.MONTHLY))
				.isEqualByComparingTo(new BigDecimal("100.00"));
	}

	@Test
	void calculatesWeeklyExpenseAsFourWeeks() {
		assertThat(calculator.monthlyEquivalent(new BigDecimal("50.00"), ExpenseFrequency.WEEKLY))
				.isEqualByComparingTo(new BigDecimal("200.00"));
	}

	@Test
	void calculatesYearlyExpenseAsTwelfth() {
		assertThat(calculator.monthlyEquivalent(new BigDecimal("1200.00"), ExpenseFrequency.YEARLY))
				.isEqualByComparingTo(new BigDecimal("100.00"));
	}

	@Test
	void ignoresOneTimeExpenseForMonthlySummary() {
		assertThat(calculator.monthlyEquivalent(new BigDecimal("999.00"), ExpenseFrequency.ONE_TIME))
				.isEqualByComparingTo(BigDecimal.ZERO);
	}
}
