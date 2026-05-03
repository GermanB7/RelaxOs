package com.tranquiloos.meals.api;

import java.util.List;

import com.tranquiloos.meals.application.MealCatalogService;
import com.tranquiloos.meals.application.MealSuggestionService;
import com.tranquiloos.meals.domain.BudgetLevel;
import com.tranquiloos.meals.domain.CravingLevel;
import com.tranquiloos.meals.domain.EffortLevel;
import com.tranquiloos.modes.domain.ModeCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/meals")
public class MealController {

	private final MealCatalogService catalogService;
	private final MealSuggestionService suggestionService;

	public MealController(MealCatalogService catalogService, MealSuggestionService suggestionService) {
		this.catalogService = catalogService;
		this.suggestionService = suggestionService;
	}

	@GetMapping("/catalog")
	public List<MealCatalogItemResponse> catalog(
			@RequestParam(required = false) EffortLevel effortLevel,
			@RequestParam(required = false) BudgetLevel budgetLevel,
			@RequestParam(required = false) CravingLevel cravingLevel,
			@RequestParam(required = false) Integer maxPrepTimeMinutes,
			@RequestParam(required = false) String equipment,
			@RequestParam(required = false) ModeCode modeCode) {
		return catalogService.catalog(effortLevel, budgetLevel, cravingLevel, maxPrepTimeMinutes, equipment, modeCode);
	}

	@PostMapping("/suggest")
	public MealSuggestionResponse suggest(@Valid @RequestBody MealSuggestionRequest request) {
		return suggestionService.suggest(request);
	}
}
