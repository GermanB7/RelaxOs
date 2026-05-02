package com.tranquiloos.scoring.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ScoreEngine {

	private static final BigDecimal ZERO = BigDecimal.ZERO;
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

	public ScoreResult calculate(ScoreInput input) {
		int score = 75;
		List<ScoreFactor> factors = new ArrayList<>();
		List<RiskFactor> risks = new ArrayList<>();

		BigDecimal monthlyIncome = defaultZero(input.monthlyIncome());
		BigDecimal monthlyExpenses = defaultZero(input.monthlyExpenses());
		BigDecimal availableMonthly = monthlyIncome.subtract(monthlyExpenses);
		BigDecimal savingsRate = ratio(availableMonthly, monthlyIncome);
		BigDecimal emergencyCoverageMonths = ratio(defaultZero(input.emergencyFundCurrent()), monthlyExpenses);
		BigDecimal rentBurden = ratio(defaultZero(input.rentMonthly()), monthlyIncome);
		BigDecimal fixedBurden = ratio(defaultZero(input.essentialMonthlyExpenses()), monthlyIncome);
		BigDecimal debtBurden = ratio(defaultZero(input.debtMonthly()), monthlyIncome);
		BigDecimal foodDeliveryBurden = ratio(defaultZero(input.foodDeliveryMonthly()), monthlyIncome);

		int rentImpact = rentImpact(rentBurden);
		score += rentImpact;
		factors.add(new ScoreFactor("rent_burden", "Rent burden", percent(rentBurden), rentImpact, new BigDecimal("0.350"),
				rentImpact < 0 ? "Rent is above the comfortable range." : "Rent is within the target range."));

		int marginImpact = availableMonthly.signum() < 0 ? -30 : availableMonthly.signum() == 0 ? -10 : 5;
		factors.add(new ScoreFactor("monthly_margin", "Monthly margin", money(availableMonthly), marginImpact, new BigDecimal("0.250"),
				availableMonthly.signum() < 0 ? "Expenses are higher than income." : "Income covers the monthly expense baseline."));

		int savingsImpact = savingsImpact(savingsRate);
		score += savingsImpact;
		factors.add(new ScoreFactor("savings_rate", "Savings rate", percent(savingsRate), savingsImpact, new BigDecimal("0.300"),
				savingsImpact < 0 ? "Available monthly cash is thin." : "Monthly available cash gives the scenario breathing room."));

		int emergencyImpact = emergencyImpact(emergencyCoverageMonths);
		score += emergencyImpact;
		factors.add(new ScoreFactor("emergency_coverage", "Emergency coverage", months(emergencyCoverageMonths), emergencyImpact, new BigDecimal("0.300"),
				emergencyImpact < 0 ? "Emergency fund coverage is low." : "Emergency fund coverage supports the scenario."));

		int fixedImpact = fixedImpact(fixedBurden);
		score += fixedImpact;
		factors.add(new ScoreFactor("fixed_burden", "Fixed burden", percent(fixedBurden), fixedImpact, new BigDecimal("0.250"),
				fixedImpact < 0 ? "Essential expenses consume a high share of income." : "Essential expenses are in a manageable range."));

		int debtImpact = debtImpact(debtBurden);
		score += debtImpact;
		factors.add(new ScoreFactor("debt_burden", "Debt burden", percent(debtBurden), debtImpact, new BigDecimal("0.150"),
				debtImpact < 0 ? "Debt payments reduce flexibility." : "Debt burden is not a major pressure in this scenario."));

		int deliveryImpact = foodDeliveryImpact(foodDeliveryBurden);
		score += deliveryImpact;
		factors.add(new ScoreFactor("food_delivery_pressure", "Food delivery pressure", percent(foodDeliveryBurden), deliveryImpact, new BigDecimal("0.100"),
				deliveryImpact < 0 ? "Food delivery spending is adding pressure." : "Food delivery pressure is low."));

		addRisks(risks, rentBurden, emergencyCoverageMonths, availableMonthly, fixedBurden, debtBurden, foodDeliveryBurden);

		int clampedScore = Math.max(0, Math.min(100, score));
		ScoreStatus status = statusFor(clampedScore);
		ConfidenceLevel confidence = input.expenseCount() >= 4 && monthlyIncome.signum() > 0 ? ConfidenceLevel.MEDIUM : ConfidenceLevel.LOW;

		return new ScoreResult(clampedScore, status, confidence, summaryFor(status), List.copyOf(factors), List.copyOf(risks));
	}

	private void addRisks(
			List<RiskFactor> risks,
			BigDecimal rentBurden,
			BigDecimal emergencyCoverageMonths,
			BigDecimal availableMonthly,
			BigDecimal fixedBurden,
			BigDecimal debtBurden,
			BigDecimal foodDeliveryBurden) {
		if (greaterThan(rentBurden, "0.35")) {
			RiskSeverity severity = greaterThan(rentBurden, "0.45") ? RiskSeverity.CRITICAL : greaterThan(rentBurden, "0.40") ? RiskSeverity.HIGH : RiskSeverity.MEDIUM;
			risks.add(new RiskFactor("HIGH_RENT_BURDEN", severity, "High rent burden", "Rent takes a high share of monthly income."));
		}
		if (emergencyCoverageMonths.compareTo(BigDecimal.ONE) < 0) {
			RiskSeverity severity = emergencyCoverageMonths.signum() <= 0 ? RiskSeverity.CRITICAL : RiskSeverity.HIGH;
			risks.add(new RiskFactor("LOW_EMERGENCY_FUND", severity, "Low emergency fund", "Emergency fund covers less than one month of expenses."));
		}
		if (availableMonthly.signum() < 0) {
			risks.add(new RiskFactor("NEGATIVE_MONTHLY_MARGIN", RiskSeverity.CRITICAL, "Negative monthly margin", "Monthly expenses are higher than income."));
		}
		if (greaterThan(fixedBurden, "0.60")) {
			RiskSeverity severity = greaterThan(fixedBurden, "0.75") ? RiskSeverity.CRITICAL : RiskSeverity.HIGH;
			risks.add(new RiskFactor("HIGH_FIXED_BURDEN", severity, "High fixed burden", "Essential expenses leave limited flexibility."));
		}
		if (greaterThan(debtBurden, "0.15")) {
			RiskSeverity severity = greaterThan(debtBurden, "0.25") ? RiskSeverity.CRITICAL : RiskSeverity.HIGH;
			risks.add(new RiskFactor("HIGH_DEBT_BURDEN", severity, "High debt burden", "Debt payments are a meaningful pressure."));
		}
		if (greaterThan(foodDeliveryBurden, "0.05")) {
			RiskSeverity severity = greaterThan(foodDeliveryBurden, "0.10") ? RiskSeverity.MEDIUM : RiskSeverity.LOW;
			risks.add(new RiskFactor("FOOD_DELIVERY_PRESSURE", severity, "Food delivery pressure", "Food delivery spending is noticeable in the monthly plan."));
		}
	}

	private int rentImpact(BigDecimal rentBurden) {
		if (rentBurden.compareTo(new BigDecimal("0.25")) <= 0) return 5;
		if (rentBurden.compareTo(new BigDecimal("0.30")) <= 0) return 0;
		if (rentBurden.compareTo(new BigDecimal("0.35")) <= 0) return -5;
		if (rentBurden.compareTo(new BigDecimal("0.40")) <= 0) return -10;
		return -20;
	}

	private int savingsImpact(BigDecimal savingsRate) {
		if (savingsRate.signum() < 0) return -30;
		if (savingsRate.compareTo(new BigDecimal("0.05")) < 0) return -20;
		if (savingsRate.compareTo(new BigDecimal("0.10")) < 0) return -10;
		if (savingsRate.compareTo(new BigDecimal("0.20")) < 0) return 5;
		return 10;
	}

	private int emergencyImpact(BigDecimal coverage) {
		if (coverage.signum() <= 0) return -25;
		if (coverage.compareTo(BigDecimal.ONE) < 0) return -20;
		if (coverage.compareTo(BigDecimal.valueOf(2)) < 0) return -10;
		if (coverage.compareTo(BigDecimal.valueOf(3)) < 0) return 0;
		return 10;
	}

	private int fixedImpact(BigDecimal fixedBurden) {
		if (greaterThan(fixedBurden, "0.75")) return -20;
		if (greaterThan(fixedBurden, "0.60")) return -10;
		if (fixedBurden.compareTo(new BigDecimal("0.50")) <= 0) return 5;
		return 0;
	}

	private int debtImpact(BigDecimal debtBurden) {
		if (greaterThan(debtBurden, "0.25")) return -20;
		if (greaterThan(debtBurden, "0.15")) return -10;
		if (greaterThan(debtBurden, "0.05")) return -5;
		return 0;
	}

	private int foodDeliveryImpact(BigDecimal burden) {
		if (greaterThan(burden, "0.10")) return -8;
		if (greaterThan(burden, "0.05")) return -4;
		return 0;
	}

	private ScoreStatus statusFor(int score) {
		if (score <= 40) return ScoreStatus.NOT_RECOMMENDED;
		if (score <= 60) return ScoreStatus.VIABLE_BUT_FRAGILE;
		if (score <= 80) return ScoreStatus.STABLE_BUT_SENSITIVE;
		return ScoreStatus.TRANQUILO;
	}

	private String summaryFor(ScoreStatus status) {
		return switch (status) {
			case NOT_RECOMMENDED -> "Not recommended yet based on the current monthly pressure and risk profile.";
			case VIABLE_BUT_FRAGILE -> "Viable only with careful controls and limited unexpected expenses.";
			case STABLE_BUT_SENSITIVE -> "Viable with control, but sensitive to unexpected expenses.";
			case TRANQUILO -> "Strong scenario with comfortable monthly room and manageable risk.";
		};
	}

	private BigDecimal ratio(BigDecimal numerator, BigDecimal denominator) {
		if (denominator == null || denominator.signum() <= 0) {
			return ZERO;
		}
		return defaultZero(numerator).divide(denominator, 6, RoundingMode.HALF_UP);
	}

	private boolean greaterThan(BigDecimal value, String threshold) {
		return value.compareTo(new BigDecimal(threshold)) > 0;
	}

	private BigDecimal defaultZero(BigDecimal value) {
		return value == null ? ZERO : value;
	}

	private String percent(BigDecimal ratio) {
		return ratio.multiply(HUNDRED).setScale(0, RoundingMode.HALF_UP) + "%";
	}

	private String months(BigDecimal months) {
		return months.setScale(1, RoundingMode.HALF_UP) + " months";
	}

	private String money(BigDecimal amount) {
		return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}
}
