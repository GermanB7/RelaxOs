package com.tranquiloos.meals.application;

import java.util.List;

import com.tranquiloos.meals.api.MealCatalogItemResponse;
import com.tranquiloos.meals.domain.BudgetLevel;
import com.tranquiloos.meals.domain.CravingLevel;
import com.tranquiloos.meals.domain.EffortLevel;
import com.tranquiloos.meals.infrastructure.MealCatalogItemEntity;
import com.tranquiloos.meals.infrastructure.MealCatalogItemJpaRepository;
import com.tranquiloos.modes.domain.ModeCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MealCatalogService {

	private final MealCatalogItemJpaRepository mealRepository;

	public MealCatalogService(MealCatalogItemJpaRepository mealRepository) {
		this.mealRepository = mealRepository;
	}

	@Transactional(readOnly = true)
	public List<MealCatalogItemResponse> catalog(
			EffortLevel effortLevel,
			BudgetLevel budgetLevel,
			CravingLevel cravingLevel,
			Integer maxPrepTimeMinutes,
			String equipment,
			ModeCode modeCode) {
		return mealRepository.findByActiveTrueOrderBySortOrderAsc()
				.stream()
				.filter(meal -> effortLevel == null || meal.getEffortLevel() == effortLevel)
				.filter(meal -> budgetLevel == null || meal.getBudgetLevel() == budgetLevel)
				.filter(meal -> cravingLevel == null || meal.getCravingLevel() == cravingLevel)
				.filter(meal -> maxPrepTimeMinutes == null || meal.getPrepTimeMinutes() <= maxPrepTimeMinutes)
				.filter(meal -> equipment == null || equipment.isBlank() || containsEquipment(meal.getRequiredEquipment(), equipment))
				.filter(meal -> modeCode == null || meal.getSuggestedMode() == modeCode)
				.map(this::toResponse)
				.toList();
	}

	MealCatalogItemResponse toResponse(MealCatalogItemEntity meal) {
		return new MealCatalogItemResponse(
				meal.getId(),
				meal.getCode(),
				meal.getName(),
				meal.getCategory(),
				meal.getEstimatedCostMin(),
				meal.getEstimatedCostMax(),
				meal.getPrepTimeMinutes(),
				meal.getEffortLevel(),
				meal.getCravingLevel(),
				meal.getBudgetLevel(),
				meal.getRequiredEquipment(),
				meal.getSuggestedMode(),
				meal.getDescription());
	}

	private boolean containsEquipment(String requiredEquipment, String equipment) {
		return requiredEquipment != null && requiredEquipment.toUpperCase().contains(equipment.toUpperCase());
	}
}
