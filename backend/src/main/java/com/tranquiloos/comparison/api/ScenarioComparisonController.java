package com.tranquiloos.comparison.api;

import com.tranquiloos.comparison.application.ScenarioComparisonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/scenarios")
public class ScenarioComparisonController {

	private final ScenarioComparisonService scenarioComparisonService;

	public ScenarioComparisonController(ScenarioComparisonService scenarioComparisonService) {
		this.scenarioComparisonService = scenarioComparisonService;
	}

	@PostMapping("/compare")
	ScenarioComparisonResponse compare(@Valid @RequestBody CompareScenariosRequest request) {
		return scenarioComparisonService.compare(request);
	}

	@PostMapping("/{scenarioId}/select")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void select(@PathVariable Long scenarioId, @RequestBody(required = false) SelectScenarioRequest request) {
		scenarioComparisonService.selectScenario(scenarioId, request);
	}
}
