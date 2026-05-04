package com.tranquiloos.decisions.api;

import java.util.List;

import com.tranquiloos.decisions.application.DecisionEventService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DecisionEventController {

	private final DecisionEventService decisionEventService;

	public DecisionEventController(DecisionEventService decisionEventService) {
		this.decisionEventService = decisionEventService;
	}

	@GetMapping("/decisions")
	List<DecisionEventResponse> listDecisions() {
		return decisionEventService.listCurrentUserEvents();
	}

	@GetMapping("/decisions/{id}")
	DecisionEventResponse getDecision(@PathVariable Long id) {
		return decisionEventService.getCurrentUserEvent(id);
	}

	@PostMapping("/decisions")
	DecisionEventResponse createDecision(@Valid @RequestBody CreateDecisionEventRequest request) {
		return decisionEventService.createManualEvent(request);
	}

	@GetMapping("/scenarios/{scenarioId}/decisions")
	List<DecisionEventResponse> listScenarioDecisions(@PathVariable Long scenarioId) {
		return decisionEventService.listCurrentUserScenarioEvents(scenarioId);
	}
}
