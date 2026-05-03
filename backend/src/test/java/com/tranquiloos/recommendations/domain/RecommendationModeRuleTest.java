package com.tranquiloos.recommendations.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.recommendations.domain.rules.AggressiveSavingPurchaseFreezeRule;
import com.tranquiloos.recommendations.domain.rules.FoodDeliveryToMealPlannerRule;
import com.tranquiloos.recommendations.domain.rules.LiveLifeBoundariesRule;
import com.tranquiloos.recommendations.domain.rules.RecoveryModeSoftenAlertsRule;
import com.tranquiloos.recommendations.domain.rules.ResetModeReviewRule;
import com.tranquiloos.recommendations.domain.rules.WarModeLowCostMealsRule;
import com.tranquiloos.recommendations.domain.rules.WarModeSpendingFocusRule;
import com.tranquiloos.scoring.domain.ConfidenceLevel;
import com.tranquiloos.scoring.domain.ScoreStatus;
import com.tranquiloos.home.infrastructure.UserPurchaseItemJpaRepository;
import org.junit.jupiter.api.Test;

class RecommendationModeRuleTest {

	@Test
	void warModeSpendingFocusTriggersCorrectly() {
		var recommendation = new WarModeSpendingFocusRule().evaluate(context(ModeCode.WAR_MODE, new BigDecimal("0.03"), BigDecimal.ZERO, 0));

		assertThat(recommendation).isPresent();
		assertThat(recommendation.get().sourceRuleKey()).isEqualTo("WAR_MODE_SPENDING_FOCUS_RULE");
	}

	@Test
	void recoveryModeSoftenAlertsTriggersCorrectly() {
		var recommendation = new RecoveryModeSoftenAlertsRule().evaluate(context(ModeCode.RECOVERY_MODE, BigDecimal.ONE, BigDecimal.ONE, 1));

		assertThat(recommendation).isPresent();
		assertThat(recommendation.get().sourceRuleKey()).isEqualTo("RECOVERY_MODE_SOFTEN_ALERTS_RULE");
	}

	@Test
	void aggressiveSavingPurchaseFreezeTriggersCorrectly() {
		UserPurchaseItemJpaRepository repository = mock(UserPurchaseItemJpaRepository.class);
		when(repository.countByUserIdAndTierAndStatus(1L, "TIER_2", "PENDING")).thenReturn(1L);

		var recommendation = new AggressiveSavingPurchaseFreezeRule(repository).evaluate(context(ModeCode.AGGRESSIVE_SAVING_MODE, BigDecimal.ONE, BigDecimal.ONE, 0));

		assertThat(recommendation).isPresent();
		assertThat(recommendation.get().sourceRuleKey()).isEqualTo("AGGRESSIVE_SAVING_PURCHASE_FREEZE_RULE");
	}

	@Test
	void liveLifeBoundariesTriggersCorrectly() {
		var recommendation = new LiveLifeBoundariesRule().evaluate(context(ModeCode.LIVE_LIFE_MODE, new BigDecimal("0.02"), BigDecimal.ONE, 0));

		assertThat(recommendation).isPresent();
		assertThat(recommendation.get().sourceRuleKey()).isEqualTo("LIVE_LIFE_BOUNDARIES_RULE");
	}

	@Test
	void resetModeReviewTriggersCorrectly() {
		var recommendation = new ResetModeReviewRule().evaluate(context(ModeCode.RESET_MODE, BigDecimal.ONE, BigDecimal.ONE, 0));

		assertThat(recommendation).isPresent();
		assertThat(recommendation.get().sourceRuleKey()).isEqualTo("RESET_MODE_REVIEW_RULE");
	}

	@Test
	void foodDeliveryPressureCreatesMealPlannerRecommendation() {
		var recommendation = new FoodDeliveryToMealPlannerRule().evaluate(context(ModeCode.STABLE_MODE, BigDecimal.ONE, BigDecimal.ONE, 0));

		assertThat(recommendation).isPresent();
		assertThat(recommendation.get().sourceRuleKey()).isEqualTo("FOOD_DELIVERY_TO_MEAL_PLANNER_RULE");
	}

	@Test
	void warModeCreatesLowCostMealsRecommendation() {
		var recommendation = new WarModeLowCostMealsRule().evaluate(context(ModeCode.WAR_MODE, BigDecimal.ONE, BigDecimal.ONE, 0));

		assertThat(recommendation).isPresent();
		assertThat(recommendation.get().sourceRuleKey()).isEqualTo("WAR_MODE_LOW_COST_MEALS_RULE");
	}

	private RecommendationContext context(ModeCode modeCode, BigDecimal savingsRate, BigDecimal emergencyCoverage, long highOpenCount) {
		return new RecommendationContext(
				1L,
				1L,
				1L,
				60,
				ScoreStatus.VIABLE_BUT_FRAGILE,
				ConfidenceLevel.MEDIUM,
				BigDecimal.valueOf(3000),
				BigDecimal.valueOf(2500),
				BigDecimal.valueOf(500),
				List.of(),
				List.of(),
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				new BigDecimal("200"),
				BigDecimal.valueOf(1500),
				BigDecimal.ZERO,
				new BigDecimal("0.50"),
				BigDecimal.ZERO,
				new BigDecimal("0.08"),
				emergencyCoverage,
				savingsRate,
				modeCode,
				modeCode.name(),
				null,
				null,
				null,
				null,
				null,
				highOpenCount);
	}
}
