package com.tranquiloos.transport.api;

import java.util.List;

import com.tranquiloos.transport.application.TransportEvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TransportController {

	private final TransportEvaluationService transportEvaluationService;

	public TransportController(TransportEvaluationService transportEvaluationService) {
		this.transportEvaluationService = transportEvaluationService;
	}

	@PostMapping("/scenarios/{scenarioId}/transport-options")
	@ResponseStatus(HttpStatus.CREATED)
	TransportOptionResponse createOption(@PathVariable Long scenarioId, @Valid @RequestBody TransportOptionRequest request) {
		return transportEvaluationService.createOption(scenarioId, request);
	}

	@GetMapping("/scenarios/{scenarioId}/transport-options")
	List<TransportOptionResponse> listOptions(@PathVariable Long scenarioId) {
		return transportEvaluationService.listOptions(scenarioId);
	}

	@PutMapping("/transport-options/{id}")
	TransportOptionResponse updateOption(@PathVariable Long id, @Valid @RequestBody TransportOptionRequest request) {
		return transportEvaluationService.updateOption(id, request);
	}

	@DeleteMapping("/transport-options/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void deleteOption(@PathVariable Long id) {
		transportEvaluationService.deleteOption(id);
	}

	@PostMapping("/scenarios/{scenarioId}/transport/evaluate")
	TransportEvaluationResponse evaluate(@PathVariable Long scenarioId) {
		return transportEvaluationService.evaluate(scenarioId);
	}

	@GetMapping("/scenarios/{scenarioId}/transport/latest")
	TransportEvaluationResponse latest(@PathVariable Long scenarioId) {
		return transportEvaluationService.latest(scenarioId);
	}
}
