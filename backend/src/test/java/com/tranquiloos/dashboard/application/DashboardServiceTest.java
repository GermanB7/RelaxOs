package com.tranquiloos.dashboard.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.tranquiloos.home.api.HomeSetupSummaryResponse;
import com.tranquiloos.home.application.HomeSetupService;
import com.tranquiloos.modes.api.ActiveModeSummaryResponse;
import com.tranquiloos.modes.application.ActiveModeProvider;
import com.tranquiloos.modes.domain.AlertPolicy;
import com.tranquiloos.modes.domain.IntensityLevel;
import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.modes.domain.PurchasePolicy;
import com.tranquiloos.modes.domain.RoutinePolicy;
import com.tranquiloos.modes.domain.SpendingPolicy;
import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationStatus;
import com.tranquiloos.recommendations.infrastructure.RecommendationEntity;
import com.tranquiloos.recommendations.infrastructure.RecommendationJpaRepository;
import com.tranquiloos.recommendations.infrastructure.DecisionEventJpaRepository;
import com.tranquiloos.scenarios.api.ScenarioResponse;
import com.tranquiloos.scenarios.api.ScenarioSummaryResponse;
import com.tranquiloos.scenarios.application.ScenarioService;
import com.tranquiloos.scenarios.domain.ScenarioStatus;
import com.tranquiloos.scoring.domain.ConfidenceLevel;
import com.tranquiloos.scoring.domain.RiskSeverity;
import com.tranquiloos.scoring.domain.ScoreStatus;
import com.tranquiloos.scoring.infrastructure.RiskFactorEntity;
import com.tranquiloos.scoring.infrastructure.RiskFactorRepository;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotEntity;
import com.tranquiloos.scoring.infrastructure.ScoreSnapshotRepository;
import com.tranquiloos.transport.infrastructure.TransportEvaluationRepository;
import com.tranquiloos.users.api.ProfileResponse;
import com.tranquiloos.users.application.CurrentUserProvider;
import com.tranquiloos.users.application.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

	@Mock
	private CurrentUserProvider currentUserProvider;

	@Mock
	private ProfileService profileService;

	@Mock
	private ScenarioService scenarioService;

	@Mock
	private ScoreSnapshotRepository scoreSnapshotRepository;

	@Mock
	private RiskFactorRepository riskFactorRepository;

	@Mock
	private RecommendationJpaRepository recommendationRepository;

	@Mock
	private ActiveModeProvider activeModeProvider;

	@Mock
	private HomeSetupService homeSetupService;

	@Mock
	private DecisionEventJpaRepository decisionEventRepository;

	@Mock
	private TransportEvaluationRepository transportEvaluationRepository;

	private DashboardService dashboardService;

	@BeforeEach
	void setUp() {
		dashboardService = new DashboardService(
				currentUserProvider,
				profileService,
				scenarioService,
				scoreSnapshotRepository,
				riskFactorRepository,
				recommendationRepository,
				activeModeProvider,
				homeSetupService,
				decisionEventRepository,
				transportEvaluationRepository);
		when(currentUserProvider.currentUserId()).thenReturn(1L);
		when(profileService.getCurrentProfile()).thenReturn(new ProfileResponse("Local User", "Bogota", "COP", null));
		when(activeModeProvider.currentSummary()).thenReturn(ActiveModeSummaryResponse.empty());
		when(recommendationRepository.findTop5ByUserIdAndStatusOrderByPriorityAscSeverityDescCreatedAtDesc(1L, RecommendationStatus.OPEN))
				.thenReturn(List.of());
	}

	@Test
	void dashboardWorksWithoutScenario() {
		when(scenarioService.listCurrentUserScenarios()).thenReturn(List.of());
		when(homeSetupService.getSummary(1L, null)).thenReturn(emptyHomeSummary());

		var dashboard = dashboardService.getDashboard();

		assertThat(dashboard.profile().displayName()).isEqualTo("Local User");
		assertThat(dashboard.primaryScenario()).isNull();
		assertThat(dashboard.latestScore()).isNull();
		assertThat(dashboard.topRecommendations()).isEmpty();
		assertThat(dashboard.homeSetup().hasRoadmap()).isFalse();
	}

	@Test
	void dashboardWorksWithScenarioButNoScore() {
		when(scenarioService.listCurrentUserScenarios()).thenReturn(List.of(scenario()));
		when(scenarioService.getSummary(10L)).thenReturn(summary());
		when(scoreSnapshotRepository.findFirstByScenarioIdOrderByCreatedAtDesc(10L)).thenReturn(Optional.empty());
		when(homeSetupService.getSummary(1L, 10L)).thenReturn(emptyHomeSummary());

		var dashboard = dashboardService.getDashboard();

		assertThat(dashboard.primaryScenario().id()).isEqualTo(10L);
		assertThat(dashboard.primaryScenario().estimatedMonthlyAvailable()).isEqualByComparingTo("1200000.00");
		assertThat(dashboard.latestScore()).isNull();
	}

	@Test
	void dashboardReturnsLatestScoreAndTopRisks() {
		ScoreSnapshotEntity snapshot = snapshot();
		when(scenarioService.listCurrentUserScenarios()).thenReturn(List.of(scenario()));
		when(scenarioService.getSummary(10L)).thenReturn(summary());
		when(scoreSnapshotRepository.findFirstByScenarioIdOrderByCreatedAtDesc(10L)).thenReturn(Optional.of(snapshot));
		when(riskFactorRepository.findByScoreSnapshotIdOrderByIdAsc(20L)).thenReturn(List.of(risk("Medium", RiskSeverity.MEDIUM), risk("Critical", RiskSeverity.CRITICAL)));
		when(homeSetupService.getSummary(1L, 10L)).thenReturn(emptyHomeSummary());

		var dashboard = dashboardService.getDashboard();

		assertThat(dashboard.latestScore().score()).isEqualTo(67);
		assertThat(dashboard.latestScore().status()).isEqualTo(ScoreStatus.STABLE_BUT_SENSITIVE);
		assertThat(dashboard.topRisks()).extracting("title").containsExactly("Critical", "Medium");
	}

	@Test
	void dashboardReturnsTopThreeRecommendations() {
		when(scenarioService.listCurrentUserScenarios()).thenReturn(List.of());
		when(homeSetupService.getSummary(1L, null)).thenReturn(emptyHomeSummary());
		when(recommendationRepository.findTop5ByUserIdAndStatusOrderByPriorityAscSeverityDescCreatedAtDesc(1L, RecommendationStatus.OPEN))
				.thenReturn(List.of(recommendation(1L), recommendation(2L), recommendation(3L), recommendation(4L)));

		var dashboard = dashboardService.getDashboard();

		assertThat(dashboard.topRecommendations()).hasSize(3);
		assertThat(dashboard.topRecommendations()).extracting("id").containsExactly(1L, 2L, 3L);
	}

	@Test
	void dashboardReturnsActiveMode() {
		when(scenarioService.listCurrentUserScenarios()).thenReturn(List.of());
		when(homeSetupService.getSummary(1L, null)).thenReturn(emptyHomeSummary());
		when(activeModeProvider.currentSummary()).thenReturn(new ActiveModeSummaryResponse(
				true,
				5L,
				ModeCode.WAR_MODE,
				"Modo Guerra",
				"Focus",
				IntensityLevel.HIGH,
				SpendingPolicy.STRICT,
				AlertPolicy.STRICT,
				PurchasePolicy.FREEZE_NON_ESSENTIAL,
				RoutinePolicy.STRICT,
				null,
				Instant.now(),
				null,
				null,
				List.of("Freeze non-essential purchases.")));

		var dashboard = dashboardService.getDashboard();

		assertThat(dashboard.activeMode().hasActiveMode()).isTrue();
		assertThat(dashboard.activeMode().modeCode()).isEqualTo(ModeCode.WAR_MODE);
		assertThat(dashboard.activeMode().guidance()).contains("Freeze non-essential purchases.");
	}

	@Test
	void dashboardReturnsHomeSetupSummary() {
		when(scenarioService.listCurrentUserScenarios()).thenReturn(List.of());
		when(homeSetupService.getSummary(1L, null)).thenReturn(new HomeSetupSummaryResponse(
				20L,
				12L,
				3L,
				1L,
				4L,
				8L,
				3L,
				37,
				BigDecimal.valueOf(1500000),
				new HomeSetupSummaryResponse.NextBestPurchaseResponse(9L, "Colchon", "TIER_1", "Bedroom", 1)));

		var dashboard = dashboardService.getDashboard();

		assertThat(dashboard.homeSetup().hasRoadmap()).isTrue();
		assertThat(dashboard.homeSetup().tier1CompletionPercentage()).isEqualTo(37);
		assertThat(dashboard.homeSetup().nextBestPurchaseName()).isEqualTo("Colchon");
		assertThat(dashboard.homeSetup().pendingItems()).isEqualTo(12L);
	}

	private ScenarioResponse scenario() {
		Instant now = Instant.now();
		return new ScenarioResponse(
				10L,
				"Vivir solo",
				new BigDecimal("3000000.00"),
				BigDecimal.ZERO,
				null,
				ScenarioStatus.DRAFT,
				now,
				now);
	}

	private ScenarioSummaryResponse summary() {
		return new ScenarioSummaryResponse(
				10L,
				new BigDecimal("3000000.00"),
				new BigDecimal("1800000.00"),
				new BigDecimal("1200000.00"),
				5L);
	}

	private ScoreSnapshotEntity snapshot() {
		ScoreSnapshotEntity snapshot = new ScoreSnapshotEntity();
		ReflectionTestUtils.setField(snapshot, "id", 20L);
		snapshot.setScenarioId(10L);
		snapshot.setScore(67);
		snapshot.setStatus(ScoreStatus.STABLE_BUT_SENSITIVE);
		snapshot.setConfidenceLevel(ConfidenceLevel.MEDIUM);
		snapshot.setSummary("Viable with control.");
		return snapshot;
	}

	private RiskFactorEntity risk(String title, RiskSeverity severity) {
		RiskFactorEntity risk = new RiskFactorEntity();
		risk.setScoreSnapshotId(20L);
		risk.setTitle(title);
		risk.setSeverity(severity);
		risk.setRiskKey(title);
		return risk;
	}

	private RecommendationEntity recommendation(Long id) {
		RecommendationEntity recommendation = new RecommendationEntity();
		ReflectionTestUtils.setField(recommendation, "id", id);
		recommendation.setUserId(1L);
		recommendation.setScenarioId(10L);
		recommendation.setSeverity(RecommendationSeverity.HIGH);
		recommendation.setTitle("Recommendation " + id);
		recommendation.setActionLabel("Open");
		recommendation.setActionType("OPEN_SCENARIO");
		recommendation.setStatus(RecommendationStatus.OPEN);
		return recommendation;
	}

	private HomeSetupSummaryResponse emptyHomeSummary() {
		return new HomeSetupSummaryResponse(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0, BigDecimal.ZERO, null);
	}
}
