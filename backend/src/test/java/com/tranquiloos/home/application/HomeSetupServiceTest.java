package com.tranquiloos.home.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.home.api.CreateCustomPurchaseItemRequest;
import com.tranquiloos.home.api.CreateHomeRoadmapRequest;
import com.tranquiloos.home.api.UpdatePurchaseStatusRequest;
import com.tranquiloos.home.api.UserPurchaseItemResponse;
import com.tranquiloos.home.domain.PurchaseStatus;
import com.tranquiloos.home.domain.PurchaseTier;
import com.tranquiloos.home.infrastructure.UserPurchaseItemEntity;
import com.tranquiloos.home.infrastructure.UserPurchaseItemJpaRepository;
import com.tranquiloos.recommendations.infrastructure.DecisionEventEntity;
import com.tranquiloos.recommendations.infrastructure.DecisionEventJpaRepository;
import com.tranquiloos.scenarios.application.ScenarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class HomeSetupServiceTest {

	@Mock
	private UserPurchaseItemJpaRepository userPurchaseRepository;

	@Mock
	private PurchaseCatalogService catalogService;

	@Mock
	private HomeSetupPriorityService priorityService;

	@Mock
	private DecisionEventJpaRepository decisionEventRepository;

	@Mock
	private ScenarioService scenarioService;

	private HomeSetupService homeSetupService;

	@BeforeEach
	void setUp() {
		homeSetupService = new HomeSetupService(
				userPurchaseRepository,
				catalogService,
				priorityService,
				decisionEventRepository,
				scenarioService,
				new ObjectMapper());
	}

	@Test
	void validatesScenarioOwnershipBeforeInitializingRoadmap() {
		when(catalogService.getAllActiveCatalogItems()).thenReturn(List.of());

		homeSetupService.initializeRoadmap(1L, new CreateHomeRoadmapRequest(10L));

		verify(scenarioService).findCurrentUserScenario(10L);
	}

	@Test
	void validatesScenarioOwnershipBeforeCreatingCustomItem() {
		when(priorityService.calculateDefaultPriority("TIER_1")).thenReturn(20);
		when(userPurchaseRepository.save(any(UserPurchaseItemEntity.class))).thenAnswer(invocation -> {
			UserPurchaseItemEntity item = invocation.getArgument(0);
			ReflectionTestUtils.setField(item, "id", 50L);
			return item;
		});

		UserPurchaseItemResponse response = homeSetupService.createCustomItem(1L, new CreateCustomPurchaseItemRequest(
				10L,
				"Air fryer",
				"Cocina",
				PurchaseTier.TIER_1,
				new BigDecimal("300000"),
				null,
				null,
				null));

		verify(scenarioService).findCurrentUserScenario(10L);
		assertThat(response.status()).isEqualTo(PurchaseStatus.PENDING.name());
		assertThat(response.tier()).isEqualTo(PurchaseTier.TIER_1.name());
	}

	@Test
	void updatesStatusUsingAllowedEnumAndCreatesDecisionEvent() {
		UserPurchaseItemEntity item = new UserPurchaseItemEntity(1L, 10L, "Colchon", "Dormir", "TIER_1");
		ReflectionTestUtils.setField(item, "id", 99L);
		item.setStatus(PurchaseStatus.PENDING.name());
		when(userPurchaseRepository.findById(99L)).thenReturn(Optional.of(item));
		when(userPurchaseRepository.save(any(UserPurchaseItemEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

		UserPurchaseItemResponse response = homeSetupService.updateItemStatus(1L, 99L, new UpdatePurchaseStatusRequest(
				PurchaseStatus.BOUGHT,
				new BigDecimal("950000"),
				"Purchased during stabilization QA"));

		ArgumentCaptor<DecisionEventEntity> eventCaptor = ArgumentCaptor.forClass(DecisionEventEntity.class);
		verify(decisionEventRepository).save(eventCaptor.capture());

		assertThat(response.status()).isEqualTo(PurchaseStatus.BOUGHT.name());
		assertThat(response.actualPrice()).isEqualByComparingTo(new BigDecimal("950000"));
		assertThat(response.purchasedAt()).isNotNull();
		assertThat(eventCaptor.getValue().getChosenOption()).isEqualTo(PurchaseStatus.BOUGHT.name());
		assertThat(eventCaptor.getValue().getContextJson()).contains("\"previousStatus\":\"PENDING\"");
		assertThat(eventCaptor.getValue().getContextJson()).contains("\"newStatus\":\"BOUGHT\"");
	}
}
