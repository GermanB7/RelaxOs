package com.tranquiloos.meals.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.tranquiloos.meals.api.MealSuggestionRequest;
import com.tranquiloos.meals.domain.BudgetLevel;
import com.tranquiloos.meals.domain.CravingLevel;
import com.tranquiloos.meals.domain.EffortLevel;
import com.tranquiloos.meals.infrastructure.MealCatalogItemEntity;
import com.tranquiloos.meals.infrastructure.MealCatalogItemJpaRepository;
import com.tranquiloos.modes.application.ActiveModeProvider;
import com.tranquiloos.modes.domain.IntensityLevel;
import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.modes.domain.ModePolicySnapshot;
import com.tranquiloos.scenarios.application.ScenarioService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class MealSuggestionServiceTest {

	@Test
	void returnsActiveMealsRankedAndLimitedConsideringActiveMode() {
		MealCatalogItemJpaRepository repository = mock(MealCatalogItemJpaRepository.class);
		ActiveModeProvider activeModeProvider = mock(ActiveModeProvider.class);
		ScenarioService scenarioService = mock(ScenarioService.class);
		MealSuggestionService service = new MealSuggestionService(repository, activeModeProvider, scenarioService);
		when(repository.findByActiveTrueOrderBySortOrderAsc()).thenReturn(List.of(
				meal(1L, "Best", EffortLevel.LOW, BudgetLevel.MEDIUM, CravingLevel.RICH, 20, ModeCode.WAR_MODE),
				meal(2L, "Slow", EffortLevel.HIGH, BudgetLevel.HIGH, CravingLevel.HEAVY, 60, null),
				meal(3L, "Good", EffortLevel.LOW, BudgetLevel.LOW, CravingLevel.RICH, 15, null)));
		when(activeModeProvider.currentPolicy()).thenReturn(Optional.of(new ModePolicySnapshot(1L, ModeCode.WAR_MODE, "Modo Guerra", IntensityLevel.HIGH, null, null, null, null)));

		var response = service.suggest(new MealSuggestionRequest(CravingLevel.RICH, 25, EffortLevel.LOW, BudgetLevel.MEDIUM, List.of("AIR_FRYER"), null));

		assertThat(response.activeModeCode()).isEqualTo(ModeCode.WAR_MODE);
		assertThat(response.suggestions()).hasSize(3);
		assertThat(response.suggestions().getFirst().name()).isEqualTo("Best");
		assertThat(response.suggestions().getFirst().fitScore()).isGreaterThanOrEqualTo(response.suggestions().get(1).fitScore());
	}

	private MealCatalogItemEntity meal(Long id, String name, EffortLevel effort, BudgetLevel budget, CravingLevel craving, int prep, ModeCode mode) {
		MealCatalogItemEntity meal = new MealCatalogItemEntity();
		ReflectionTestUtils.setField(meal, "id", id);
		ReflectionTestUtils.setField(meal, "code", name.toLowerCase());
		ReflectionTestUtils.setField(meal, "name", name);
		ReflectionTestUtils.setField(meal, "category", "Test");
		ReflectionTestUtils.setField(meal, "estimatedCostMin", BigDecimal.ONE);
		ReflectionTestUtils.setField(meal, "estimatedCostMax", BigDecimal.TEN);
		ReflectionTestUtils.setField(meal, "prepTimeMinutes", prep);
		ReflectionTestUtils.setField(meal, "effortLevel", effort);
		ReflectionTestUtils.setField(meal, "budgetLevel", budget);
		ReflectionTestUtils.setField(meal, "cravingLevel", craving);
		ReflectionTestUtils.setField(meal, "requiredEquipment", "Air fryer");
		ReflectionTestUtils.setField(meal, "suggestedMode", mode);
		return meal;
	}
}
