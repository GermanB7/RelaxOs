package com.tranquiloos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.tranquiloos.admin.infrastructure.AdminAuditLogRepository;
import com.tranquiloos.admin.infrastructure.RecommendationCopyRepository;
import com.tranquiloos.expenses.infrastructure.ExpenseCategoryRepository;
import com.tranquiloos.expenses.infrastructure.ScenarioExpenseRepository;
import com.tranquiloos.home.infrastructure.PurchaseCatalogItemJpaRepository;
import com.tranquiloos.home.infrastructure.UserPurchaseItemJpaRepository;
import com.tranquiloos.meals.infrastructure.MealCatalogItemJpaRepository;
import com.tranquiloos.modes.infrastructure.AdaptiveModeJpaRepository;
import com.tranquiloos.modes.infrastructure.ModeActivationJpaRepository;
import com.tranquiloos.recommendations.infrastructure.DecisionEventJpaRepository;
import com.tranquiloos.recommendations.infrastructure.RecommendationJpaRepository;
import com.tranquiloos.scenarios.infrastructure.ScenarioRepository;
import com.tranquiloos.scoring.infrastructure.RiskFactorRepository;
import com.tranquiloos.scoring.infrastructure.ScoreFactorRepository;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotRepository;
import com.tranquiloos.settings.infrastructure.SystemSettingRepository;
import com.tranquiloos.transport.infrastructure.TransportEvaluationRepository;
import com.tranquiloos.transport.infrastructure.TransportOptionRepository;
import com.tranquiloos.users.infrastructure.AppUserRepository;
import com.tranquiloos.users.infrastructure.UserProfileRepository;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
				+ "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
				+ "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
})
class TranquiloosApplicationTests {

	@MockitoBean
	private AppUserRepository appUserRepository;

	@MockitoBean
	private UserProfileRepository userProfileRepository;

	@MockitoBean
	private ScenarioRepository scenarioRepository;

	@MockitoBean
	private ExpenseCategoryRepository expenseCategoryRepository;

	@MockitoBean
	private ScenarioExpenseRepository scenarioExpenseRepository;

	@MockitoBean
	private ScoreSnapshotRepository scoreSnapshotRepository;

	@MockitoBean
	private ScoreFactorRepository scoreFactorRepository;

	@MockitoBean
	private RiskFactorRepository riskFactorRepository;

	@MockitoBean
	private RecommendationJpaRepository recommendationJpaRepository;

	@MockitoBean
	private DecisionEventJpaRepository decisionEventJpaRepository;

	@MockitoBean
	private PurchaseCatalogItemJpaRepository purchaseCatalogItemJpaRepository;

	@MockitoBean
	private UserPurchaseItemJpaRepository userPurchaseItemJpaRepository;

	@MockitoBean
	private AdaptiveModeJpaRepository adaptiveModeJpaRepository;

	@MockitoBean
	private ModeActivationJpaRepository modeActivationJpaRepository;

	@MockitoBean
	private MealCatalogItemJpaRepository mealCatalogItemJpaRepository;

	@MockitoBean
	private TransportOptionRepository transportOptionRepository;

	@MockitoBean
	private TransportEvaluationRepository transportEvaluationRepository;

	@MockitoBean
	private SystemSettingRepository systemSettingRepository;

	@MockitoBean
	private AdminAuditLogRepository adminAuditLogRepository;

	@MockitoBean
	private RecommendationCopyRepository recommendationCopyRepository;

	@Test
	void contextLoads() {
	}
}
