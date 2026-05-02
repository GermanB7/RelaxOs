package com.tranquiloos.recommendations.domain;

public final class RulePriority {

	public static final int NEGATIVE_MARGIN = 1;
	public static final int LOW_EMERGENCY_FUND = 2;
	public static final int HIGH_RENT_BURDEN = 3;
	public static final int HIGH_FIXED_BURDEN = 4;
	public static final int HIGH_DEBT_BURDEN = 5;
	public static final int FOOD_DELIVERY_PRESSURE = 6;
	public static final int DATA_QUALITY = 10;

	private RulePriority() {
	}
}
