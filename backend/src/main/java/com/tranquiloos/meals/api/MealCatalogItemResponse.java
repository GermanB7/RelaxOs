package com.tranquiloos.meals.api;

import java.math.BigDecimal;

import com.tranquiloos.meals.domain.BudgetLevel;
import com.tranquiloos.meals.domain.CravingLevel;
import com.tranquiloos.meals.domain.EffortLevel;
import com.tranquiloos.modes.domain.ModeCode;

public record MealCatalogItemResponse(
		Long id,
		String code,
		String name,
		String category,
		BigDecimal estimatedCostMin,
		BigDecimal estimatedCostMax,
		Integer prepTimeMinutes,
		EffortLevel effortLevel,
		CravingLevel cravingLevel,
		BudgetLevel budgetLevel,
		String requiredEquipment,
		ModeCode suggestedMode,
		String description) {
}
