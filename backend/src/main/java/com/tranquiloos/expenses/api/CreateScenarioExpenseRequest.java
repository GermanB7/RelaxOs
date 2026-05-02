package com.tranquiloos.expenses.api;

import java.math.BigDecimal;

import com.tranquiloos.expenses.domain.ExpenseFrequency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateScenarioExpenseRequest(
		@NotNull Long categoryId,
		@NotBlank @Size(max = 160) String name,
		@NotNull @PositiveOrZero BigDecimal amount,
		@NotNull ExpenseFrequency frequency,
		@NotNull Boolean isEssential) {
}
