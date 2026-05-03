package com.tranquiloos.meals.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.tranquiloos.meals.domain.MealFitCalculator.MealFitInput;
import com.tranquiloos.modes.domain.ModeCode;
import org.junit.jupiter.api.Test;

class MealFitCalculatorTest {

	private final MealFitCalculator calculator = new MealFitCalculator();

	@Test
	void matchingCriteriaGivesHighScore() {
		int score = calculator.calculate(input(EffortLevel.LOW, BudgetLevel.MEDIUM, CravingLevel.RICH, 20, 25, BudgetLevel.MEDIUM));

		assertThat(score).isGreaterThanOrEqualTo(85);
	}

	@Test
	void overMaxPrepTimePenalizes() {
		int score = calculator.calculate(inputWithoutEquipmentBonus(EffortLevel.LOW, BudgetLevel.MEDIUM, CravingLevel.RICH, 40, 20, BudgetLevel.MEDIUM));

		assertThat(score).isLessThan(100);
	}

	@Test
	void lowBudgetRequestPenalizesHighBudgetMeal() {
		int score = calculator.calculate(inputWithoutEquipmentBonus(EffortLevel.LOW, BudgetLevel.HIGH, CravingLevel.RICH, 20, 25, BudgetLevel.LOW));
		int matchingBudgetScore = calculator.calculate(inputWithoutEquipmentBonus(EffortLevel.LOW, BudgetLevel.LOW, CravingLevel.RICH, 20, 25, BudgetLevel.LOW));

		assertThat(score).isLessThan(matchingBudgetScore);
	}

	@Test
	void clampsBetweenZeroAndOneHundred() {
		assertThat(calculator.calculate(input(EffortLevel.LOW, BudgetLevel.MEDIUM, CravingLevel.RICH, 20, 25, BudgetLevel.MEDIUM))).isBetween(0, 100);
	}

	private MealFitInput input(EffortLevel mealEffort, BudgetLevel mealBudget, CravingLevel mealCraving, int prepTime, int maxPrep, BudgetLevel requestedBudget) {
		return new MealFitInput(
				mealEffort,
				mealBudget,
				mealCraving,
				prepTime,
				"Air fryer",
				"WAR_MODE",
				EffortLevel.LOW,
				requestedBudget,
				CravingLevel.RICH,
				maxPrep,
				List.of("AIR_FRYER"),
				ModeCode.WAR_MODE);
	}

	private MealFitInput inputWithoutEquipmentBonus(EffortLevel mealEffort, BudgetLevel mealBudget, CravingLevel mealCraving, int prepTime, int maxPrep, BudgetLevel requestedBudget) {
		return new MealFitInput(
				mealEffort,
				mealBudget,
				mealCraving,
				prepTime,
				"Air fryer",
				null,
				EffortLevel.LOW,
				requestedBudget,
				CravingLevel.RICH,
				maxPrep,
				List.of("STOVE"),
				null);
	}
}
