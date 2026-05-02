package com.tranquiloos.scoring.application;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.expenses.api.ScenarioExpenseResponse;
import com.tranquiloos.expenses.application.ExpenseService;
import com.tranquiloos.scenarios.application.ScenarioService;
import com.tranquiloos.scenarios.infrastructure.ScenarioEntity;
import com.tranquiloos.scoring.api.RiskFactorResponse;
import com.tranquiloos.scoring.api.ScoreFactorResponse;
import com.tranquiloos.scoring.api.ScoreHistoryResponse;
import com.tranquiloos.scoring.api.ScoreResponse;
import com.tranquiloos.scoring.domain.RiskFactor;
import com.tranquiloos.scoring.domain.ScoreEngine;
import com.tranquiloos.scoring.domain.ScoreFactor;
import com.tranquiloos.scoring.domain.ScoreInput;
import com.tranquiloos.scoring.domain.ScoreResult;
import com.tranquiloos.scoring.infrastructure.RiskFactorEntity;
import com.tranquiloos.scoring.infrastructure.RiskFactorRepository;
import com.tranquiloos.scoring.infrastructure.ScoreFactorEntity;
import com.tranquiloos.scoring.infrastructure.ScoreFactorRepository;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotEntity;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotRepository;
import com.tranquiloos.shared.error.ResourceNotFoundException;

@Service
public class ScenarioScoreService {

	private final ScenarioService scenarioService;
	private final ExpenseService expenseService;
	private final ScoreSnapshotRepository snapshotRepository;
	private final ScoreFactorRepository factorRepository;
	private final RiskFactorRepository riskRepository;
	private final ObjectMapper objectMapper;
	private final ScoreEngine scoreEngine = new ScoreEngine();

	public ScenarioScoreService(
			ScenarioService scenarioService,
			ExpenseService expenseService,
			ScoreSnapshotRepository snapshotRepository,
			ScoreFactorRepository factorRepository,
			RiskFactorRepository riskRepository,
			ObjectMapper objectMapper) {
		this.scenarioService = scenarioService;
		this.expenseService = expenseService;
		this.snapshotRepository = snapshotRepository;
		this.factorRepository = factorRepository;
		this.riskRepository = riskRepository;
		this.objectMapper = objectMapper;
	}

	@Transactional
	public ScoreResponse calculateScore(Long scenarioId) {
		ScenarioEntity scenario = scenarioService.findCurrentUserScenario(scenarioId);
		List<ScenarioExpenseResponse> expenses = expenseService.listScenarioExpenses(scenario.getId());
		ScoreInput input = toInput(scenario, expenses);
		ScoreResult result = scoreEngine.calculate(input);

		ScoreSnapshotEntity snapshot = new ScoreSnapshotEntity();
		snapshot.setScenarioId(scenario.getId());
		snapshot.setScore(result.score());
		snapshot.setStatus(result.status());
		snapshot.setConfidenceLevel(result.confidenceLevel());
		snapshot.setSummary(result.summary());
		snapshot.setInputSnapshotJson(toJson(input));
		ScoreSnapshotEntity savedSnapshot = snapshotRepository.save(snapshot);

		List<ScoreFactorEntity> savedFactors = result.factors()
				.stream()
				.map(factor -> factorRepository.save(toEntity(savedSnapshot.getId(), factor)))
				.toList();
		List<RiskFactorEntity> savedRisks = result.risks()
				.stream()
				.map(risk -> riskRepository.save(toEntity(savedSnapshot.getId(), risk)))
				.toList();

		return toResponse(savedSnapshot, savedFactors, savedRisks);
	}

	@Transactional(readOnly = true)
	public ScoreResponse getLatestScore(Long scenarioId) {
		scenarioService.findCurrentUserScenario(scenarioId);
		ScoreSnapshotEntity snapshot = snapshotRepository.findFirstByScenarioIdOrderByCreatedAtDesc(scenarioId)
				.orElseThrow(() -> new ResourceNotFoundException("Scenario score was not found"));
		return toResponse(
				snapshot,
				factorRepository.findByScoreSnapshotIdOrderByIdAsc(snapshot.getId()),
				riskRepository.findByScoreSnapshotIdOrderByIdAsc(snapshot.getId()));
	}

	@Transactional(readOnly = true)
	public List<ScoreHistoryResponse> getScoreHistory(Long scenarioId) {
		scenarioService.findCurrentUserScenario(scenarioId);
		return snapshotRepository.findTop10ByScenarioIdOrderByCreatedAtDesc(scenarioId)
				.stream()
				.map(snapshot -> new ScoreHistoryResponse(
						snapshot.getId(),
						snapshot.getScenarioId(),
						snapshot.getScore(),
						snapshot.getStatus(),
						snapshot.getConfidenceLevel(),
						snapshot.getSummary(),
						snapshot.getCreatedAt()))
				.toList();
	}

	private ScoreInput toInput(ScenarioEntity scenario, List<ScenarioExpenseResponse> expenses) {
		BigDecimal monthlyExpenses = expenses.stream()
				.map(ScenarioExpenseResponse::monthlyEquivalent)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal rentMonthly = sumByCategory(expenses, "rent");
		BigDecimal debtMonthly = sumByCategory(expenses, "debt");
		BigDecimal foodDeliveryMonthly = sumByCategory(expenses, "food_delivery");
		BigDecimal essentialMonthly = expenses.stream()
				.filter(ScenarioExpenseResponse::isEssential)
				.map(ScenarioExpenseResponse::monthlyEquivalent)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return new ScoreInput(
				scenario.getId(),
				scenario.getMonthlyIncome(),
				scenario.getEmergencyFundCurrent(),
				monthlyExpenses,
				rentMonthly,
				essentialMonthly,
				debtMonthly,
				foodDeliveryMonthly,
				expenses.size(),
				expenses.stream().map(ScenarioExpenseResponse::categoryCode).distinct().toList());
	}

	private BigDecimal sumByCategory(List<ScenarioExpenseResponse> expenses, String categoryCode) {
		return expenses.stream()
				.filter(expense -> categoryCode.equals(expense.categoryCode()))
				.map(ScenarioExpenseResponse::monthlyEquivalent)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private ScoreFactorEntity toEntity(Long snapshotId, ScoreFactor factor) {
		ScoreFactorEntity entity = new ScoreFactorEntity();
		entity.setScoreSnapshotId(snapshotId);
		entity.setFactorKey(factor.key());
		entity.setLabel(factor.label());
		entity.setValueText(factor.valueText());
		entity.setImpact(factor.impact());
		entity.setWeight(factor.weight());
		entity.setExplanation(factor.explanation());
		return entity;
	}

	private RiskFactorEntity toEntity(Long snapshotId, RiskFactor risk) {
		RiskFactorEntity entity = new RiskFactorEntity();
		entity.setScoreSnapshotId(snapshotId);
		entity.setRiskKey(risk.key());
		entity.setSeverity(risk.severity());
		entity.setTitle(risk.title());
		entity.setExplanation(risk.explanation());
		return entity;
	}

	private ScoreResponse toResponse(
			ScoreSnapshotEntity snapshot,
			List<ScoreFactorEntity> factors,
			List<RiskFactorEntity> risks) {
		return new ScoreResponse(
				snapshot.getId(),
				snapshot.getScenarioId(),
				snapshot.getScore(),
				snapshot.getStatus(),
				snapshot.getConfidenceLevel(),
				snapshot.getSummary(),
				factors.stream().map(this::toResponse).toList(),
				risks.stream().map(this::toResponse).toList(),
				snapshot.getCreatedAt());
	}

	private ScoreFactorResponse toResponse(ScoreFactorEntity factor) {
		return new ScoreFactorResponse(
				factor.getFactorKey(),
				factor.getLabel(),
				factor.getValueText(),
				factor.getImpact(),
				factor.getWeight(),
				factor.getExplanation());
	}

	private RiskFactorResponse toResponse(RiskFactorEntity risk) {
		return new RiskFactorResponse(risk.getRiskKey(), risk.getSeverity(), risk.getTitle(), risk.getExplanation());
	}

	private String toJson(ScoreInput input) {
		try {
			return objectMapper.writeValueAsString(input);
		} catch (JsonProcessingException exception) {
			return "{}";
		}
	}
}
