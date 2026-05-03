package com.tranquiloos.meals.api;

import java.math.BigDecimal;
import java.util.List;

import com.tranquiloos.meals.domain.BudgetLevel;
import com.tranquiloos.meals.domain.CravingLevel;
import com.tranquiloos.meals.domain.EffortLevel;
import com.tranquiloos.modes.domain.ModeCode;

public record MealSuggestionResponse(
		ModeCode activeModeCode,
		List<Suggestion> suggestions) {

	public record Suggestion(
			Long id,
			String name,
			BigDecimal estimatedCostMin,
			BigDecimal estimatedCostMax,
			Integer prepTimeMinutes,
			EffortLevel effortLevel,
			CravingLevel cravingLevel,
			BudgetLevel budgetLevel,
			String requiredEquipment,
			int fitScore,
			String reason) {
	}
}
