package com.tranquiloos.expenses.api;

import java.util.List;

import com.tranquiloos.scenarios.application.ScenarioService;
import com.tranquiloos.expenses.application.ExpenseService;

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
@RequestMapping("/api/v1/scenarios/{scenarioId}/expenses")
public class ScenarioExpenseController {

	private final ScenarioService scenarioService;
	private final ExpenseService expenseService;

	public ScenarioExpenseController(ScenarioService scenarioService, ExpenseService expenseService) {
		this.scenarioService = scenarioService;
		this.expenseService = expenseService;
	}

	@GetMapping
	List<ScenarioExpenseResponse> listExpenses(@PathVariable Long scenarioId) {
		scenarioService.findCurrentUserScenario(scenarioId);
		return expenseService.listScenarioExpenses(scenarioId);
	}

	@PostMapping
	ScenarioExpenseResponse createExpense(
			@PathVariable Long scenarioId,
			@Valid @RequestBody CreateScenarioExpenseRequest request) {
		scenarioService.findCurrentUserScenario(scenarioId);
		return expenseService.createExpense(scenarioId, request);
	}

	@PutMapping("/{expenseId}")
	ScenarioExpenseResponse updateExpense(
			@PathVariable Long scenarioId,
			@PathVariable Long expenseId,
			@Valid @RequestBody UpdateScenarioExpenseRequest request) {
		scenarioService.findCurrentUserScenario(scenarioId);
		return expenseService.updateExpense(scenarioId, expenseId, request);
	}

	@DeleteMapping("/{expenseId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void deleteExpense(@PathVariable Long scenarioId, @PathVariable Long expenseId) {
		scenarioService.findCurrentUserScenario(scenarioId);
		expenseService.deleteExpense(scenarioId, expenseId);
	}
}
