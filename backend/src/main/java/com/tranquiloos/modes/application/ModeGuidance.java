package com.tranquiloos.modes.application;

import java.util.List;

import com.tranquiloos.modes.domain.ModeCode;

final class ModeGuidance {

	private ModeGuidance() {
	}

	static List<String> guidance(ModeCode code) {
		return switch (code) {
			case WAR_MODE -> List.of(
					"Freeze non-essential purchases.",
					"Prioritize savings, focus, and cooking at home.",
					"Review finances frequently.");
			case STABLE_MODE -> List.of(
					"Keep steady savings.",
					"Allow planned purchases.",
					"Review finances weekly or biweekly.");
			case LIVE_LIFE_MODE -> List.of(
					"Allow conscious flexibility.",
					"Protect emergency fund and fixed obligations.",
					"Avoid debt for leisure.");
			case RECOVERY_MODE -> List.of(
					"Use minimum viable routines.",
					"Avoid aggressive alerts.",
					"Postpone major financial decisions.");
			case AGGRESSIVE_SAVING_MODE -> List.of(
					"Freeze comfort and luxury purchases.",
					"Push surplus toward emergency fund or independence goal.",
					"Reduce flexible spending temporarily.");
			case RESET_MODE -> List.of(
					"Review budget, home roadmap, and pending recommendations.",
					"Clean up wishlist and scenario assumptions.",
					"Keep it short and concrete.");
		};
	}
}
