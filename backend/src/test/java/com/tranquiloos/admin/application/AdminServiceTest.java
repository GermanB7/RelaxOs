package com.tranquiloos.admin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.admin.api.AdminDtos;
import com.tranquiloos.admin.infrastructure.AdminAuditLogEntity;
import com.tranquiloos.admin.infrastructure.AdminAuditLogRepository;
import com.tranquiloos.admin.infrastructure.RecommendationCopyRepository;
import com.tranquiloos.expenses.infrastructure.ExpenseCategoryEntity;
import com.tranquiloos.expenses.infrastructure.ExpenseCategoryRepository;
import com.tranquiloos.home.domain.ImpactLevel;
import com.tranquiloos.home.domain.PurchaseTier;
import com.tranquiloos.home.domain.UrgencyLevel;
import com.tranquiloos.home.infrastructure.PurchaseCatalogItemJpaRepository;
import com.tranquiloos.meals.infrastructure.MealCatalogItemJpaRepository;
import com.tranquiloos.modes.infrastructure.AdaptiveModeJpaRepository;
import com.tranquiloos.settings.infrastructure.SystemSettingEntity;
import com.tranquiloos.settings.infrastructure.SystemSettingRepository;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

	@Mock
	private CurrentUserProvider currentUserProvider;

	@Mock
	private ExpenseCategoryRepository expenseCategoryRepository;

	@Mock
	private PurchaseCatalogItemJpaRepository purchaseRepository;

	@Mock
	private MealCatalogItemJpaRepository mealRepository;

	@Mock
	private AdaptiveModeJpaRepository modeRepository;

	@Mock
	private SystemSettingRepository settingRepository;

	@Mock
	private RecommendationCopyRepository recommendationCopyRepository;

	@Mock
	private AdminAuditLogRepository auditRepository;

	private AdminService adminService;

	@BeforeEach
	void setUp() {
		adminService = new AdminService(
				currentUserProvider,
				expenseCategoryRepository,
				purchaseRepository,
				mealRepository,
				modeRepository,
				settingRepository,
				recommendationCopyRepository,
				auditRepository,
				new ObjectMapper());
		when(currentUserProvider.currentUserId()).thenReturn(1L);
	}

	@Test
	void overviewReturnsCatalogCounts() {
		when(expenseCategoryRepository.count()).thenReturn(3L);
		when(purchaseRepository.count()).thenReturn(4L);
		when(mealRepository.count()).thenReturn(5L);
		when(modeRepository.count()).thenReturn(6L);
		when(settingRepository.count()).thenReturn(7L);
		when(auditRepository.count()).thenReturn(8L);

		var overview = adminService.overview();

		assertThat(overview.expenseCategories()).isEqualTo(3L);
		assertThat(overview.purchaseItems()).isEqualTo(4L);
		assertThat(overview.auditEvents()).isEqualTo(8L);
	}

	@Test
	void expenseCategoryCreateAndToggleAreAudited() {
		when(expenseCategoryRepository.findByCodeIgnoreCase("parking")).thenReturn(Optional.empty());
		when(expenseCategoryRepository.save(any(ExpenseCategoryEntity.class))).thenAnswer(invocation -> {
			ExpenseCategoryEntity entity = invocation.getArgument(0);
			ReflectionTestUtils.setField(entity, "id", 20L);
			return entity;
		});
		var created = adminService.createExpenseCategory(new AdminDtos.ExpenseCategoryRequest("parking", "Parking", null, true, 50));
		when(expenseCategoryRepository.findById(20L)).thenReturn(Optional.of(category(20L, "parking", true)));

		var toggled = adminService.toggleExpenseCategory(20L);

		assertThat(created.code()).isEqualTo("parking");
		assertThat(toggled.active()).isFalse();
		verify(auditRepository, times(2)).save(any(AdminAuditLogEntity.class));
	}

	@Test
	void settingCreateAndExportWork() {
		when(settingRepository.existsById("rent.safe.max")).thenReturn(false);
		when(settingRepository.save(any(SystemSettingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(settingRepository.findAllByOrderByKeyAsc()).thenReturn(List.of(setting("rent.safe.max", "0.30")));

		var created = adminService.createSetting(new AdminDtos.SettingRequest("rent.safe.max", "0.30", "DECIMAL", "Safe rent", true));
		Object exported = adminService.exportCatalog("settings");

		assertThat(created.key()).isEqualTo("rent.safe.max");
		assertThat((List<?>) exported).hasSize(1);
		verify(auditRepository).save(any(AdminAuditLogEntity.class));
	}

	@Test
	void importValidatesInvalidDataBeforePersisting() {
		var result = adminService.importCatalog("expense-categories", new AdminDtos.ImportRequest(List.of(Map.of("name", "Missing code"))));

		assertThat(result.importedCount()).isZero();
		assertThat(result.errors()).isNotEmpty();
		verify(expenseCategoryRepository, never()).save(any());
	}

	@Test
	void purchaseValidationRejectsDuplicateCode() {
		when(purchaseRepository.findByCodeIgnoreCase("mattress")).thenReturn(Optional.of(new com.tranquiloos.home.infrastructure.PurchaseCatalogItemEntity()));

		try {
			adminService.createPurchaseItem(new AdminDtos.PurchaseItemRequest(
					"mattress",
					"Mattress",
					"Sleep",
					PurchaseTier.TIER_1,
					null,
					null,
					ImpactLevel.HIGH,
					UrgencyLevel.HIGH,
					null,
					null,
					null,
					null,
					true,
					10));
		} catch (IllegalArgumentException exception) {
			assertThat(exception.getMessage()).contains("already exists");
		}
		verify(purchaseRepository, never()).save(any());
	}

	private ExpenseCategoryEntity category(Long id, String code, boolean active) {
		ExpenseCategoryEntity entity = new ExpenseCategoryEntity();
		ReflectionTestUtils.setField(entity, "id", id);
		entity.setCode(code);
		entity.setName(code);
		entity.setActive(active);
		entity.setSortOrder(100);
		return entity;
	}

	private SystemSettingEntity setting(String key, String value) {
		SystemSettingEntity entity = new SystemSettingEntity();
		entity.setKey(key);
		entity.setValue(value);
		entity.setValueType("DECIMAL");
		entity.setActive(true);
		return entity;
	}
}
