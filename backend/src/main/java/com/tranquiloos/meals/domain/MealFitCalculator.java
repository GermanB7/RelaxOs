package com.tranquiloos.meals.domain;

import java.util.List;

import com.tranquiloos.modes.domain.ModeCode;

public class MealFitCalculator {

	public int calculate(MealFitInput input) {
		int score = 50;
		if (input.mealEffort() == input.requestedEffort()) {
			score += 20;
		}
		if (input.mealBudget() == input.requestedBudget()) {
			score += 15;
		}
		if (input.mealCraving() == input.requestedCraving()) {
			score += 15;
		}
		if (input.maxPrepTimeMinutes() != null && input.prepTimeMinutes() <= input.maxPrepTimeMinutes()) {
			score += 10;
		}
		if (equipmentOverlaps(input.requiredEquipment(), input.availableEquipment())) {
			score += 10;
		}
		if (input.suggestedMode() != null && input.activeMode() != null && input.suggestedMode().equals(input.activeMode().name())) {
			score += 5;
		}
		if (input.maxPrepTimeMinutes() != null && input.prepTimeMinutes() > input.maxPrepTimeMinutes()) {
			score -= 15;
		}
		if (input.mealEffort() == EffortLevel.HIGH && input.requestedEffort() == EffortLevel.LOW) {
			score -= 10;
		}
		if (input.mealBudget() == BudgetLevel.HIGH && input.requestedBudget() == BudgetLevel.LOW) {
			score -= 10;
		}
		return Math.max(0, Math.min(100, score));
	}

	public String reason(MealFitInput input, int fitScore) {
		if (fitScore >= 85) {
			return "Fast, tasty, low effort, and compatible with your equipment.";
		}
		if (input.activeMode() != null && input.suggestedMode() != null && input.suggestedMode().equals(input.activeMode().name())) {
			return "Good fit for your active mode and practical constraints.";
		}
		if (input.maxPrepTimeMinutes() != null && input.prepTimeMinutes() > input.maxPrepTimeMinutes()) {
			return "Useful option, but it may take longer than your requested time.";
		}
		return "Practical option based on craving, effort, budget, and available equipment.";
	}

	private boolean equipmentOverlaps(String requiredEquipment, List<String> availableEquipment) {
		if (requiredEquipment == null || availableEquipment == null || availableEquipment.isEmpty()) {
			return false;
		}
		String normalizedRequired = normalizeEquipment(requiredEquipment);
		return availableEquipment.stream()
				.filter(equipment -> equipment != null && !equipment.isBlank())
				.map(this::normalizeEquipment)
				.anyMatch(normalizedRequired::contains);
	}

	private String normalizeEquipment(String equipment) {
		return equipment.toUpperCase().replace('_', ' ');
	}

	public record MealFitInput(
			EffortLevel mealEffort,
			BudgetLevel mealBudget,
			CravingLevel mealCraving,
			int prepTimeMinutes,
			String requiredEquipment,
			String suggestedMode,
			EffortLevel requestedEffort,
			BudgetLevel requestedBudget,
			CravingLevel requestedCraving,
			Integer maxPrepTimeMinutes,
			List<String> availableEquipment,
			ModeCode activeMode) {
	}
}
