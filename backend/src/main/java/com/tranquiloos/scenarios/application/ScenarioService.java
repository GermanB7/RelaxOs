package com.tranquiloos.scenarios.application;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tranquiloos.expenses.application.ExpenseService;
import com.tranquiloos.scenarios.api.CreateScenarioRequest;
import com.tranquiloos.scenarios.api.ScenarioResponse;
import com.tranquiloos.scenarios.api.ScenarioSummaryResponse;
import com.tranquiloos.scenarios.api.UpdateScenarioRequest;
import com.tranquiloos.scenarios.domain.ScenarioStatus;
import com.tranquiloos.scenarios.infrastructure.ScenarioEntity;
import com.tranquiloos.scenarios.infrastructure.ScenarioRepository;
import com.tranquiloos.shared.error.ResourceNotFoundException;
import com.tranquiloos.users.application.CurrentUserProvider;

@Service
public class ScenarioService {

	private final CurrentUserProvider currentUserProvider;
	private final ScenarioRepository scenarioRepository;
	private final ExpenseService expenseService;

	public ScenarioService(
			CurrentUserProvider currentUserProvider,
			ScenarioRepository scenarioRepository,
			ExpenseService expenseService) {
		this.currentUserProvider = currentUserProvider;
		this.scenarioRepository = scenarioRepository;
		this.expenseService = expenseService;
	}

	@Transactional(readOnly = true)
	public List<ScenarioResponse> listCurrentUserScenarios() {
		return scenarioRepository.findByUserIdOrderByUpdatedAtDesc(currentUserProvider.currentUserId())
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public ScenarioResponse getScenario(Long scenarioId) {
		return toResponse(findCurrentUserScenario(scenarioId));
	}

	@Transactional
	public ScenarioResponse createScenario(CreateScenarioRequest request) {
		ScenarioEntity scenario = new ScenarioEntity();
		scenario.setUserId(currentUserProvider.currentUserId());
		scenario.setName(request.name());
		scenario.setMonthlyIncome(request.monthlyIncome());
		scenario.setEmergencyFundCurrent(defaultZero(request.emergencyFundCurrent()));
		scenario.setEmergencyFundTarget(request.emergencyFundTarget());
		scenario.setStatus(ScenarioStatus.DRAFT);
		return toResponse(scenarioRepository.save(scenario));
	}

	@Transactional
	public ScenarioResponse updateScenario(Long scenarioId, UpdateScenarioRequest request) {
		ScenarioEntity scenario = findCurrentUserScenario(scenarioId);
		scenario.setName(request.name());
		scenario.setMonthlyIncome(request.monthlyIncome());
		scenario.setEmergencyFundCurrent(defaultZero(request.emergencyFundCurrent()));
		scenario.setEmergencyFundTarget(request.emergencyFundTarget());
		scenario.setStatus(request.status());
		return toResponse(scenarioRepository.save(scenario));
	}

	@Transactional
	public ScenarioResponse duplicateScenario(Long scenarioId) {
		ScenarioEntity original = findCurrentUserScenario(scenarioId);
		ScenarioEntity copy = new ScenarioEntity();
		copy.setUserId(original.getUserId());
		copy.setName(original.getName() + " Copy");
		copy.setMonthlyIncome(original.getMonthlyIncome());
		copy.setEmergencyFundCurrent(original.getEmergencyFundCurrent());
		copy.setEmergencyFundTarget(original.getEmergencyFundTarget());
		copy.setStatus(ScenarioStatus.DRAFT);
		ScenarioEntity savedCopy = scenarioRepository.save(copy);
		expenseService.copyExpenses(original.getId(), savedCopy.getId());
		return toResponse(savedCopy);
	}

	@Transactional(readOnly = true)
	public ScenarioSummaryResponse getSummary(Long scenarioId) {
		ScenarioEntity scenario = findCurrentUserScenario(scenarioId);
		BigDecimal totalMonthlyExpenses = expenseService.totalMonthlyExpenses(scenario.getId());
		long expenseCount = expenseService.countScenarioExpenses(scenario.getId());
		return new ScenarioSummaryResponse(
				scenario.getId(),
				scenario.getMonthlyIncome(),
				totalMonthlyExpenses,
				scenario.getMonthlyIncome().subtract(totalMonthlyExpenses),
				expenseCount);
	}

	public ScenarioEntity findCurrentUserScenario(Long scenarioId) {
		return scenarioRepository.findByIdAndUserId(scenarioId, currentUserProvider.currentUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Scenario was not found"));
	}

	private BigDecimal defaultZero(BigDecimal value) {
		return value == null ? BigDecimal.ZERO : value;
	}

	private ScenarioResponse toResponse(ScenarioEntity scenario) {
		return new ScenarioResponse(
				scenario.getId(),
				scenario.getName(),
				scenario.getMonthlyIncome(),
				scenario.getEmergencyFundCurrent(),
				scenario.getEmergencyFundTarget(),
				scenario.getStatus(),
				scenario.getCreatedAt(),
				scenario.getUpdatedAt());
	}
}
