package com.tranquiloos.dashboard.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.scoring.domain.RiskSeverity;
import com.tranquiloos.scoring.domain.ScoreStatus;
import com.tranquiloos.transport.domain.TransportOptionType;
import com.tranquiloos.transport.domain.TransportRiskLevel;

public record DashboardSummaryResponse(
		ActiveScenario activeScenario,
		Integer latestScore,
		ScoreStatus scoreStatus,
		ActiveMode activeMode,
		FinancialSnapshot financialSnapshot,
		RiskSummary mainRisk,
		List<RecommendationSummary> topRecommendations,
		ScenarioComparisonSummary scenarioComparisonSummary,
		TransportSummary transportSummary,
		HomePriority homePriority,
		MealSuggestionSummary mealSuggestion,
		List<DecisionEventSummary> recentDecisionEvents,
		List<QuickAction> quickActions) {

	public record ActiveScenario(Long id, String name) {
	}

	public record ActiveMode(boolean hasActiveMode, ModeCode modeCode, String modeName, Long scenarioId, List<String> guidance) {
	}

	public record FinancialSnapshot(
			BigDecimal monthlyIncome,
			BigDecimal monthlyExpenses,
			BigDecimal estimatedSavings,
			BigDecimal savingsRate,
			BigDecimal rentBurden,
			BigDecimal fixedBurden,
			BigDecimal transportBurden,
			BigDecimal emergencyCoverage) {
	}

	public record RiskSummary(RiskSeverity severity, String title, String explanation) {
	}

	public record RecommendationSummary(
			Long id,
			Long scenarioId,
			RecommendationSeverity severity,
			String title,
			String message,
			String actionLabel,
			String actionType) {
	}

	public record ScenarioComparisonSummary(boolean available, Integer scenarioCount, Long lastRecommendedScenarioId, String lastReason) {
	}

	public record TransportSummary(
			boolean available,
			TransportOptionType recommendedCurrentOption,
			TransportOptionType futureViableOption,
			BigDecimal transportBurden,
			Integer fitScore,
			TransportRiskLevel riskLevel,
			String explanation,
			String conditionsToSwitch) {
	}

	public record HomePriority(boolean hasRoadmap, String nextBestPurchaseName, Integer tier1CompletionPercentage, Long pendingItems) {
	}

	public record MealSuggestionSummary(String title, String reason) {
	}

	public record DecisionEventSummary(
			Long id,
			Long scenarioId,
			String decisionType,
			String question,
			String chosenOption,
			String reason,
			Instant createdAt) {
	}

	public record QuickAction(String key, String label, String path, String priority) {
	}
}
