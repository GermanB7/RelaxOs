package com.tranquiloos.scenarios.api;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tranquiloos.scenarios.application.ScenarioService;

@RestController
@RequestMapping("/api/v1/scenarios")
public class ScenarioController {

	private final ScenarioService scenarioService;

	public ScenarioController(ScenarioService scenarioService) {
		this.scenarioService = scenarioService;
	}

	@GetMapping
	List<ScenarioResponse> listScenarios() {
		return scenarioService.listCurrentUserScenarios();
	}

	@PostMapping
	ScenarioResponse createScenario(@Valid @RequestBody CreateScenarioRequest request) {
		return scenarioService.createScenario(request);
	}

	@GetMapping("/{id}")
	ScenarioResponse getScenario(@PathVariable Long id) {
		return scenarioService.getScenario(id);
	}

	@PutMapping("/{id}")
	ScenarioResponse updateScenario(@PathVariable Long id, @Valid @RequestBody UpdateScenarioRequest request) {
		return scenarioService.updateScenario(id, request);
	}

	@PostMapping("/{id}/duplicate")
	ScenarioResponse duplicateScenario(@PathVariable Long id) {
		return scenarioService.duplicateScenario(id);
	}

	@GetMapping("/{id}/summary")
	ScenarioSummaryResponse getSummary(@PathVariable Long id) {
		return scenarioService.getSummary(id);
	}
}
