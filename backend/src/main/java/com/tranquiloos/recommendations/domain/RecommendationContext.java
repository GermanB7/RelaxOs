package com.tranquiloos.recommendations.domain;

import java.math.BigDecimal;
import java.util.List;

import com.tranquiloos.expenses.api.ScenarioExpenseResponse;
import com.tranquiloos.scoring.domain.ConfidenceLevel;
import com.tranquiloos.scoring.domain.ScoreStatus;

public record RecommendationContext(
		Long userId,
		Long scenarioId,
		Long scoreSnapshotId,
		int score,
		ScoreStatus scoreStatus,
		ConfidenceLevel confidenceLevel,
		BigDecimal monthlyIncome,
		BigDecimal totalMonthlyExpenses,
		BigDecimal estimatedMonthlyAvailable,
		List<String> latestRisks,
		List<ScenarioExpenseResponse> scenarioExpenses,
		BigDecimal rentMonthly,
		BigDecimal debtMonthly,
		BigDecimal foodDeliveryMonthly,
		BigDecimal essentialMonthly,
		BigDecimal rentBurden,
		BigDecimal fixedBurden,
		BigDecimal debtBurden,
		BigDecimal foodDeliveryBurden,
		BigDecimal emergencyCoverageMonths,
		BigDecimal savingsRate) {

	public boolean hasRisk(String riskKey) {
		return latestRisks.contains(riskKey);
	}
}
