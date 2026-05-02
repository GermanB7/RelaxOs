package com.tranquiloos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.tranquiloos.expenses.infrastructure.ExpenseCategoryRepository;
import com.tranquiloos.expenses.infrastructure.ScenarioExpenseRepository;
import com.tranquiloos.scenarios.infrastructure.ScenarioRepository;
import com.tranquiloos.scoring.infrastructure.RiskFactorRepository;
import com.tranquiloos.scoring.infrastructure.ScoreFactorRepository;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotRepository;
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

	@Test
	void contextLoads() {
	}
}
