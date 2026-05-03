package com.tranquiloos.meals.application;

import java.util.Comparator;
import java.util.List;

import com.tranquiloos.meals.api.MealSuggestionRequest;
import com.tranquiloos.meals.api.MealSuggestionResponse;
import com.tranquiloos.meals.domain.MealFitCalculator;
import com.tranquiloos.meals.domain.MealFitCalculator.MealFitInput;
import com.tranquiloos.meals.infrastructure.MealCatalogItemEntity;
import com.tranquiloos.meals.infrastructure.MealCatalogItemJpaRepository;
import com.tranquiloos.modes.application.ActiveModeProvider;
import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.scenarios.application.ScenarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MealSuggestionService {

	private final MealCatalogItemJpaRepository mealRepository;
	private final ActiveModeProvider activeModeProvider;
	private final ScenarioService scenarioService;
	private final MealFitCalculator calculator = new MealFitCalculator();

	public MealSuggestionService(
			MealCatalogItemJpaRepository mealRepository,
			ActiveModeProvider activeModeProvider,
			ScenarioService scenarioService) {
		this.mealRepository = mealRepository;
		this.activeModeProvider = activeModeProvider;
		this.scenarioService = scenarioService;
	}

	@Transactional(readOnly = true)
	public MealSuggestionResponse suggest(MealSuggestionRequest request) {
		if (request.scenarioId() != null) {
			scenarioService.findCurrentUserScenario(request.scenarioId());
		}
		ModeCode activeMode = activeModeProvider.currentPolicy()
				.map(policy -> policy.modeCode())
				.orElse(null);
		List<MealSuggestionResponse.Suggestion> suggestions = mealRepository.findByActiveTrueOrderBySortOrderAsc()
				.stream()
				.map(meal -> toSuggestion(meal, request, activeMode))
				.sorted(Comparator.comparing(MealSuggestionResponse.Suggestion::fitScore).reversed())
				.limit(5)
				.toList();
		return new MealSuggestionResponse(activeMode, suggestions);
	}

	private MealSuggestionResponse.Suggestion toSuggestion(MealCatalogItemEntity meal, MealSuggestionRequest request, ModeCode activeMode) {
		MealFitInput input = new MealFitInput(
				meal.getEffortLevel(),
				meal.getBudgetLevel(),
				meal.getCravingLevel(),
				meal.getPrepTimeMinutes(),
				meal.getRequiredEquipment(),
				meal.getSuggestedMode() == null ? null : meal.getSuggestedMode().name(),
				request.effortLevel(),
				request.budgetLevel(),
				request.cravingLevel(),
				request.maxPrepTimeMinutes(),
				request.availableEquipment(),
				activeMode);
		int fitScore = calculator.calculate(input);
		return new MealSuggestionResponse.Suggestion(
				meal.getId(),
				meal.getName(),
				meal.getEstimatedCostMin(),
				meal.getEstimatedCostMax(),
				meal.getPrepTimeMinutes(),
				meal.getEffortLevel(),
				meal.getCravingLevel(),
				meal.getBudgetLevel(),
				meal.getRequiredEquipment(),
				fitScore,
				calculator.reason(input, fitScore));
	}
}
