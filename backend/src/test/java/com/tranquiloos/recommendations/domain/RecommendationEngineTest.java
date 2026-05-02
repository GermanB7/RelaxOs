package com.tranquiloos.recommendations.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.tranquiloos.expenses.api.ScenarioExpenseResponse;
import com.tranquiloos.recommendations.domain.rules.DataQualityRecommendationRule;
import com.tranquiloos.recommendations.domain.rules.FoodDeliveryPressureRecommendationRule;
import com.tranquiloos.recommendations.domain.rules.HighRentBurdenRecommendationRule;
import com.tranquiloos.recommendations.domain.rules.LowEmergencyFundRecommendationRule;
import com.tranquiloos.recommendations.domain.rules.NegativeMarginRecommendationRule;
import com.tranquiloos.scoring.domain.ConfidenceLevel;
import com.tranquiloos.scoring.domain.ScoreStatus;

class RecommendationEngineTest {

	@Test
	void negativeMarginCreatesCriticalPriorityOneRecommendation() {
		Optional<RecommendationCandidate> recommendation = new NegativeMarginRecommendationRule()
				.evaluate(context(BigDecimal.valueOf(-200), BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO, ConfidenceLevel.MEDIUM, 4));

		assertThat(recommendation).isPresent();
		assertThat(recommendation.get().severity()).isEqualTo(RecommendationSeverity.CRITICAL);
		assertThat(recommendation.get().priority()).isEqualTo(1);
		assertThat(recommendation.get().sourceRuleKey()).isEqualTo("NEGATIVE_MARGIN_RULE");
	}

	@Test
	void lowEmergencyFundCreatesRecommendation() {
		Optional<RecommendationCandidate> recommendation = new LowEmergencyFundRecommendationRule()
				.evaluate(context(BigDecimal.valueOf(500), new BigDecimal("0.50"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, ConfidenceLevel.MEDIUM, 4));

		assertThat(recommendation).isPresent();
		assertThat(recommendation.get().type()).isEqualTo(RecommendationType.EMERGENCY_FUND);
		assertThat(recommendation.get().sourceRuleKey()).isEqualTo("LOW_EMERGENCY_FUND_RULE");
	}

	@Test
	void highRentBurdenCreatesRecommendation() {
		Optional<RecommendationCandidate> recommendation = new HighRentBurdenRecommendationRule()
				.evaluate(context(BigDecimal.valueOf(500), BigDecimal.valueOf(2), new BigDecimal("0.41"), BigDecimal.ZERO, BigDecimal.ZERO, ConfidenceLevel.MEDIUM, 4));

		assertThat(recommendation).isPresent();
		assertThat(recommendation.get().type()).isEqualTo(RecommendationType.RENT);
		assertThat(recommendation.get().severity()).isEqualTo(RecommendationSeverity.HIGH);
	}

	@Test
	void foodDeliveryPressureCreatesRecommendation() {
		Optional<RecommendationCandidate> recommendation = new FoodDeliveryPressureRecommendationRule()
				.evaluate(context(BigDecimal.valueOf(500), BigDecimal.valueOf(2), BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("0.06"), ConfidenceLevel.MEDIUM, 4));

		assertThat(recommendation).isPresent();
		assertThat(recommendation.get().type()).isEqualTo(RecommendationType.FOOD_DELIVERY);
		assertThat(recommendation.get().sourceRuleKey()).isEqualTo("FOOD_DELIVERY_PRESSURE_RULE");
	}

	@Test
	void lowConfidenceCreatesDataQualityRecommendation() {
		Optional<RecommendationCandidate> recommendation = new DataQualityRecommendationRule()
				.evaluate(context(BigDecimal.valueOf(500), BigDecimal.valueOf(2), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, ConfidenceLevel.LOW, 2));

		assertThat(recommendation).isPresent();
		assertThat(recommendation.get().type()).isEqualTo(RecommendationType.DATA_QUALITY);
		assertThat(recommendation.get().priority()).isEqualTo(10);
	}

	private RecommendationContext context(
			BigDecimal estimatedAvailable,
			BigDecimal emergencyCoverageMonths,
			BigDecimal rentBurden,
			BigDecimal debtBurden,
			BigDecimal foodDeliveryBurden,
			ConfidenceLevel confidenceLevel,
			int expenseCount) {
		return new RecommendationContext(
				1L,
				1L,
				1L,
				60,
				ScoreStatus.VIABLE_BUT_FRAGILE,
				confidenceLevel,
				BigDecimal.valueOf(3000),
				BigDecimal.valueOf(2500),
				estimatedAvailable,
				List.of(),
				scenarioExpenses(expenseCount),
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				BigDecimal.valueOf(1500),
				rentBurden,
				new BigDecimal("0.50"),
				debtBurden,
				foodDeliveryBurden,
				emergencyCoverageMonths,
				new BigDecimal("0.16"));
	}

	private List<ScenarioExpenseResponse> scenarioExpenses(int count) {
		return java.util.stream.LongStream.rangeClosed(1, count)
				.mapToObj(id -> new ScenarioExpenseResponse(
						id,
						1L,
						id,
						"category_" + id,
						"Category " + id,
						"Expense " + id,
						BigDecimal.TEN,
						com.tranquiloos.expenses.domain.ExpenseFrequency.MONTHLY,
						true,
						BigDecimal.TEN))
				.toList();
	}
}
