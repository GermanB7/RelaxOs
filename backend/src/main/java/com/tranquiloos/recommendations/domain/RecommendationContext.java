package com.tranquiloos.recommendations.domain;

import java.math.BigDecimal;
import java.util.List;

import com.tranquiloos.expenses.api.ScenarioExpenseResponse;
import com.tranquiloos.modes.domain.AlertPolicy;
import com.tranquiloos.modes.domain.IntensityLevel;
import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.modes.domain.PurchasePolicy;
import com.tranquiloos.modes.domain.RoutinePolicy;
import com.tranquiloos.modes.domain.SpendingPolicy;
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
		BigDecimal savingsRate,
		ModeCode activeModeCode,
		String activeModeName,
		SpendingPolicy spendingPolicy,
		AlertPolicy alertPolicy,
		PurchasePolicy purchasePolicy,
		RoutinePolicy routinePolicy,
		IntensityLevel modeIntensityLevel,
		long highOpenRecommendationCount) {

	public boolean hasRisk(String riskKey) {
		return latestRisks.contains(riskKey);
	}

	public boolean isMode(ModeCode modeCode) {
		return activeModeCode == modeCode;
	}
}
