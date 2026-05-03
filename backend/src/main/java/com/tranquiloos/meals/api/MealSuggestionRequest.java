package com.tranquiloos.meals.api;

import java.util.List;

import com.tranquiloos.meals.domain.BudgetLevel;
import com.tranquiloos.meals.domain.CravingLevel;
import com.tranquiloos.meals.domain.EffortLevel;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MealSuggestionRequest(
		@NotNull CravingLevel cravingLevel,
		@Min(1) @Max(180) Integer maxPrepTimeMinutes,
		@NotNull EffortLevel effortLevel,
		@NotNull BudgetLevel budgetLevel,
		List<String> availableEquipment,
		Long scenarioId) {
}
