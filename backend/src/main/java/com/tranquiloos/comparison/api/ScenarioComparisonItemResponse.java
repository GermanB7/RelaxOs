package com.tranquiloos.comparison.api;

import java.math.BigDecimal;

import com.tranquiloos.scoring.domain.RiskSeverity;
import com.tranquiloos.scoring.domain.ScoreStatus;

public record ScenarioComparisonItemResponse(
		Long scenarioId,
		String name,
		BigDecimal monthlyIncome,
		BigDecimal monthlyExpenses,
		BigDecimal estimatedSavings,
		BigDecimal savingsRate,
		BigDecimal rentBurden,
		BigDecimal fixedBurden,
		BigDecimal emergencyCoverage,
		Integer latestScore,
		ScoreStatus scoreStatus,
		Boolean scoreMissing,
		Boolean scoreStale,
		RiskSeverity mainRiskSeverity,
		String mainRisk,
		String mainRecommendation,
		Integer decisionScore) {
}
