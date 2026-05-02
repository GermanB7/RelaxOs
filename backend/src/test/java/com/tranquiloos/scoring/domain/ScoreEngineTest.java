package com.tranquiloos.scoring.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

class ScoreEngineTest {

	private final ScoreEngine scoreEngine = new ScoreEngine();

	@Test
	void healthyScenarioReturnsStrongScore() {
		ScoreResult result = scoreEngine.calculate(new ScoreInput(
				1L,
				new BigDecimal("5000000"),
				new BigDecimal("9000000"),
				new BigDecimal("2500000"),
				new BigDecimal("1200000"),
				new BigDecimal("2200000"),
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				5,
				List.of("rent", "utilities", "internet", "groceries", "transport")));

		assertThat(result.score()).isGreaterThanOrEqualTo(70);
		assertThat(result.confidenceLevel()).isEqualTo(ConfidenceLevel.MEDIUM);
	}

	@Test
	void negativeMonthlyMarginReturnsLowScoreAndRisk() {
		ScoreResult result = scoreEngine.calculate(new ScoreInput(
				1L,
				new BigDecimal("2000000"),
				new BigDecimal("500000"),
				new BigDecimal("3200000"),
				new BigDecimal("1200000"),
				new BigDecimal("2800000"),
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				4,
				List.of("rent", "utilities", "internet", "groceries")));

		assertThat(result.score()).isLessThanOrEqualTo(40);
		assertThat(result.risks()).extracting(RiskFactor::key).contains("NEGATIVE_MONTHLY_MARGIN");
	}

	@Test
	void highRentBurdenCreatesRisk() {
		ScoreResult result = scoreEngine.calculate(new ScoreInput(
				1L,
				new BigDecimal("3000000"),
				new BigDecimal("3000000"),
				new BigDecimal("1800000"),
				new BigDecimal("1300000"),
				new BigDecimal("1600000"),
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				4,
				List.of("rent", "utilities", "internet", "groceries")));

		assertThat(result.risks()).extracting(RiskFactor::key).contains("HIGH_RENT_BURDEN");
	}

	@Test
	void lowEmergencyFundCreatesRisk() {
		ScoreResult result = scoreEngine.calculate(new ScoreInput(
				1L,
				new BigDecimal("3500000"),
				new BigDecimal("100000"),
				new BigDecimal("1800000"),
				new BigDecimal("900000"),
				new BigDecimal("1600000"),
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				4,
				List.of("rent", "utilities", "internet", "groceries")));

		assertThat(result.risks()).extracting(RiskFactor::key).contains("LOW_EMERGENCY_FUND");
	}

	@Test
	void scoreIsClampedBetweenZeroAndOneHundred() {
		ScoreResult low = scoreEngine.calculate(new ScoreInput(
				1L,
				new BigDecimal("1000000"),
				BigDecimal.ZERO,
				new BigDecimal("6000000"),
				new BigDecimal("3000000"),
				new BigDecimal("5500000"),
				new BigDecimal("1000000"),
				new BigDecimal("500000"),
				6,
				List.of()));
		ScoreResult high = scoreEngine.calculate(new ScoreInput(
				1L,
				new BigDecimal("10000000"),
				new BigDecimal("50000000"),
				new BigDecimal("1200000"),
				new BigDecimal("1000000"),
				new BigDecimal("1200000"),
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				6,
				List.of()));

		assertThat(low.score()).isBetween(0, 100);
		assertThat(high.score()).isBetween(0, 100);
	}
}
