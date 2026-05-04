package com.tranquiloos.admin.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.admin.api.AdminDtos;
import com.tranquiloos.admin.infrastructure.AdminAuditLogEntity;
import com.tranquiloos.admin.infrastructure.AdminAuditLogRepository;
import com.tranquiloos.admin.infrastructure.RecommendationCopyEntity;
import com.tranquiloos.admin.infrastructure.RecommendationCopyRepository;
import com.tranquiloos.expenses.infrastructure.ExpenseCategoryEntity;
import com.tranquiloos.expenses.infrastructure.ExpenseCategoryRepository;
import com.tranquiloos.home.infrastructure.PurchaseCatalogItemEntity;
import com.tranquiloos.home.infrastructure.PurchaseCatalogItemJpaRepository;
import com.tranquiloos.meals.infrastructure.MealCatalogItemEntity;
import com.tranquiloos.meals.infrastructure.MealCatalogItemJpaRepository;
import com.tranquiloos.modes.infrastructure.AdaptiveModeEntity;
import com.tranquiloos.modes.infrastructure.AdaptiveModeJpaRepository;
import com.tranquiloos.settings.infrastructure.SystemSettingEntity;
import com.tranquiloos.settings.infrastructure.SystemSettingRepository;
import com.tranquiloos.shared.error.ResourceNotFoundException;
import com.tranquiloos.users.application.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

	private final CurrentUserProvider currentUserProvider;
	private final ExpenseCategoryRepository expenseCategoryRepository;
	private final PurchaseCatalogItemJpaRepository purchaseRepository;
	private final MealCatalogItemJpaRepository mealRepository;
	private final AdaptiveModeJpaRepository modeRepository;
	private final SystemSettingRepository settingRepository;
	private final RecommendationCopyRepository recommendationCopyRepository;
	private final AdminAuditLogRepository auditRepository;
	private final ObjectMapper objectMapper;

	public AdminService(
			CurrentUserProvider currentUserProvider,
			ExpenseCategoryRepository expenseCategoryRepository,
			PurchaseCatalogItemJpaRepository purchaseRepository,
			MealCatalogItemJpaRepository mealRepository,
			AdaptiveModeJpaRepository modeRepository,
			SystemSettingRepository settingRepository,
			RecommendationCopyRepository recommendationCopyRepository,
			AdminAuditLogRepository auditRepository,
			ObjectMapper objectMapper) {
		this.currentUserProvider = currentUserProvider;
		this.expenseCategoryRepository = expenseCategoryRepository;
		this.purchaseRepository = purchaseRepository;
		this.mealRepository = mealRepository;
		this.modeRepository = modeRepository;
		this.settingRepository = settingRepository;
		this.recommendationCopyRepository = recommendationCopyRepository;
		this.auditRepository = auditRepository;
		this.objectMapper = objectMapper;
	}

	@Transactional(readOnly = true)
	public AdminDtos.OverviewResponse overview() {
		requireAdmin();
		return new AdminDtos.OverviewResponse(
				expenseCategoryRepository.count(),
				purchaseRepository.count(),
				mealRepository.count(),
				modeRepository.count(),
				settingRepository.count(),
				auditRepository.count());
	}

	@Transactional(readOnly = true)
	public List<AdminDtos.ExpenseCategoryResponse> listExpenseCategories() {
		requireAdmin();
		return expenseCategoryRepository.findAllByOrderBySortOrderAscNameAsc().stream().map(this::toExpenseCategory).toList();
	}

	@Transactional
	public AdminDtos.ExpenseCategoryResponse createExpenseCategory(AdminDtos.ExpenseCategoryRequest request) {
		requireAdmin();
		expenseCategoryRepository.findByCodeIgnoreCase(request.code()).ifPresent(existing -> {
			throw new IllegalArgumentException("Expense category code already exists.");
		});
		ExpenseCategoryEntity entity = new ExpenseCategoryEntity();
		apply(entity, request);
		ExpenseCategoryEntity saved = expenseCategoryRepository.save(entity);
		audit("CREATE", "EXPENSE_CATEGORY", saved.getId(), "Created expense category " + saved.getCode(), null, saved);
		return toExpenseCategory(saved);
	}

	@Transactional
	public AdminDtos.ExpenseCategoryResponse updateExpenseCategory(Long id, AdminDtos.ExpenseCategoryRequest request) {
		requireAdmin();
		ExpenseCategoryEntity entity = expenseCategoryRepository.findById(id).orElseThrow(() -> notFound("Expense category"));
		String before = json(entity);
		apply(entity, request);
		ExpenseCategoryEntity saved = expenseCategoryRepository.save(entity);
		audit("UPDATE", "EXPENSE_CATEGORY", saved.getId(), "Updated expense category " + saved.getCode(), before, saved);
		return toExpenseCategory(saved);
	}

	@Transactional
	public AdminDtos.ExpenseCategoryResponse toggleExpenseCategory(Long id) {
		requireAdmin();
		ExpenseCategoryEntity entity = expenseCategoryRepository.findById(id).orElseThrow(() -> notFound("Expense category"));
		String before = json(entity);
		entity.setActive(!entity.isActive());
		ExpenseCategoryEntity saved = expenseCategoryRepository.save(entity);
		audit("TOGGLE_ACTIVE", "EXPENSE_CATEGORY", saved.getId(), "Toggled expense category " + saved.getCode(), before, saved);
		return toExpenseCategory(saved);
	}

	@Transactional(readOnly = true)
	public List<AdminDtos.PurchaseItemResponse> listPurchaseItems() {
		requireAdmin();
		return purchaseRepository.findAllByOrderByTierAscSortOrderAscCategoryAscNameAsc().stream().map(this::toPurchaseItem).toList();
	}

	@Transactional
	public AdminDtos.PurchaseItemResponse createPurchaseItem(AdminDtos.PurchaseItemRequest request) {
		requireAdmin();
		purchaseRepository.findByCodeIgnoreCase(request.code()).ifPresent(existing -> {
			throw new IllegalArgumentException("Purchase item code already exists.");
		});
		PurchaseCatalogItemEntity entity = new PurchaseCatalogItemEntity();
		apply(entity, request);
		PurchaseCatalogItemEntity saved = purchaseRepository.save(entity);
		audit("CREATE", "PURCHASE_ITEM", saved.getId(), "Created purchase item " + saved.getCode(), null, saved);
		return toPurchaseItem(saved);
	}

	@Transactional
	public AdminDtos.PurchaseItemResponse updatePurchaseItem(Long id, AdminDtos.PurchaseItemRequest request) {
		requireAdmin();
		PurchaseCatalogItemEntity entity = purchaseRepository.findById(id).orElseThrow(() -> notFound("Purchase item"));
		String before = json(entity);
		apply(entity, request);
		PurchaseCatalogItemEntity saved = purchaseRepository.save(entity);
		audit("UPDATE", "PURCHASE_ITEM", saved.getId(), "Updated purchase item " + saved.getCode(), before, saved);
		return toPurchaseItem(saved);
	}

	@Transactional
	public AdminDtos.PurchaseItemResponse togglePurchaseItem(Long id) {
		requireAdmin();
		PurchaseCatalogItemEntity entity = purchaseRepository.findById(id).orElseThrow(() -> notFound("Purchase item"));
		String before = json(entity);
		entity.setIsActive(!entity.getIsActive());
		PurchaseCatalogItemEntity saved = purchaseRepository.save(entity);
		audit("TOGGLE_ACTIVE", "PURCHASE_ITEM", saved.getId(), "Toggled purchase item " + saved.getCode(), before, saved);
		return toPurchaseItem(saved);
	}

	@Transactional(readOnly = true)
	public List<AdminDtos.MealItemResponse> listMealItems() {
		requireAdmin();
		return mealRepository.findAllByOrderBySortOrderAscNameAsc().stream().map(this::toMealItem).toList();
	}

	@Transactional
	public AdminDtos.MealItemResponse createMealItem(AdminDtos.MealItemRequest request) {
		requireAdmin();
		mealRepository.findByCodeIgnoreCase(request.code()).ifPresent(existing -> {
			throw new IllegalArgumentException("Meal item code already exists.");
		});
		MealCatalogItemEntity entity = new MealCatalogItemEntity();
		apply(entity, request);
		MealCatalogItemEntity saved = mealRepository.save(entity);
		audit("CREATE", "MEAL_ITEM", saved.getId(), "Created meal item " + saved.getCode(), null, saved);
		return toMealItem(saved);
	}

	@Transactional
	public AdminDtos.MealItemResponse updateMealItem(Long id, AdminDtos.MealItemRequest request) {
		requireAdmin();
		MealCatalogItemEntity entity = mealRepository.findById(id).orElseThrow(() -> notFound("Meal item"));
		String before = json(entity);
		apply(entity, request);
		MealCatalogItemEntity saved = mealRepository.save(entity);
		audit("UPDATE", "MEAL_ITEM", saved.getId(), "Updated meal item " + saved.getCode(), before, saved);
		return toMealItem(saved);
	}

	@Transactional
	public AdminDtos.MealItemResponse toggleMealItem(Long id) {
		requireAdmin();
		MealCatalogItemEntity entity = mealRepository.findById(id).orElseThrow(() -> notFound("Meal item"));
		String before = json(entity);
		entity.setActive(!entity.isActive());
		MealCatalogItemEntity saved = mealRepository.save(entity);
		audit("TOGGLE_ACTIVE", "MEAL_ITEM", saved.getId(), "Toggled meal item " + saved.getCode(), before, saved);
		return toMealItem(saved);
	}

	@Transactional(readOnly = true)
	public List<AdminDtos.ModeResponse> listModes() {
		requireAdmin();
		return modeRepository.findAllByOrderBySortOrderAscNameAsc().stream().map(this::toMode).toList();
	}

	@Transactional
	public AdminDtos.ModeResponse updateMode(Long id, AdminDtos.ModeRequest request) {
		requireAdmin();
		AdaptiveModeEntity entity = modeRepository.findById(id).orElseThrow(() -> notFound("Mode"));
		String before = json(entity);
		apply(entity, request);
		AdaptiveModeEntity saved = modeRepository.save(entity);
		audit("UPDATE", "MODE", saved.getId(), "Updated mode " + saved.getCode(), before, saved);
		return toMode(saved);
	}

	@Transactional(readOnly = true)
	public List<AdminDtos.RecommendationCopyResponse> listRecommendationCopy() {
		requireAdmin();
		return recommendationCopyRepository.findAllByOrderByRuleKeyAsc().stream().map(this::toRecommendationCopy).toList();
	}

	@Transactional
	public AdminDtos.RecommendationCopyResponse updateRecommendationCopy(String ruleKey, AdminDtos.RecommendationCopyRequest request) {
		requireAdmin();
		RecommendationCopyEntity entity = recommendationCopyRepository.findById(ruleKey).orElseGet(() -> {
			RecommendationCopyEntity created = new RecommendationCopyEntity();
			created.setRuleKey(ruleKey);
			return created;
		});
		String before = entity.getTitle() == null ? null : json(entity);
		entity.setTitle(request.title());
		entity.setMessage(request.message());
		entity.setActionLabel(request.actionLabel());
		entity.setSeverity(request.severity());
		entity.setActive(request.active() == null || request.active());
		RecommendationCopyEntity saved = recommendationCopyRepository.save(entity);
		audit(before == null ? "CREATE" : "UPDATE", "RECOMMENDATION_COPY", saved.getRuleKey(), "Updated recommendation copy " + saved.getRuleKey(), before, saved);
		return toRecommendationCopy(saved);
	}

	@Transactional(readOnly = true)
	public List<AdminDtos.SettingResponse> listSettings() {
		requireAdmin();
		return settingRepository.findAllByOrderByKeyAsc().stream().map(this::toSetting).toList();
	}

	@Transactional
	public AdminDtos.SettingResponse createSetting(AdminDtos.SettingRequest request) {
		requireAdmin();
		if (settingRepository.existsById(request.key())) {
			throw new IllegalArgumentException("System setting key already exists.");
		}
		SystemSettingEntity entity = new SystemSettingEntity();
		apply(entity, request);
		SystemSettingEntity saved = settingRepository.save(entity);
		audit("CREATE", "SYSTEM_SETTING", saved.getKey(), "Created system setting " + saved.getKey(), null, saved);
		return toSetting(saved);
	}

	@Transactional
	public AdminDtos.SettingResponse updateSetting(String key, AdminDtos.SettingRequest request) {
		requireAdmin();
		SystemSettingEntity entity = settingRepository.findById(key).orElseThrow(() -> notFound("System setting"));
		String before = json(entity);
		apply(entity, request);
		entity.setKey(key);
		SystemSettingEntity saved = settingRepository.save(entity);
		audit("UPDATE", "SYSTEM_SETTING", saved.getKey(), "Updated system setting " + saved.getKey(), before, saved);
		return toSetting(saved);
	}

	@Transactional
	public AdminDtos.ImportResultResponse importCatalog(String catalogType, AdminDtos.ImportRequest request) {
		requireAdmin();
		List<String> errors = validateImport(catalogType, request);
		if (!errors.isEmpty()) {
			return new AdminDtos.ImportResultResponse(catalogType, 0, errors);
		}
		int imported = 0;
		for (Map<String, Object> item : request.items()) {
			if ("expense-categories".equals(catalogType)) {
				createExpenseCategory(objectMapper.convertValue(item, AdminDtos.ExpenseCategoryRequest.class));
			} else if ("purchase-items".equals(catalogType)) {
				createPurchaseItem(objectMapper.convertValue(item, AdminDtos.PurchaseItemRequest.class));
			} else if ("meal-items".equals(catalogType)) {
				createMealItem(objectMapper.convertValue(item, AdminDtos.MealItemRequest.class));
			} else if ("settings".equals(catalogType)) {
				createSetting(objectMapper.convertValue(item, AdminDtos.SettingRequest.class));
			}
			imported++;
		}
		audit("IMPORT", catalogType.toUpperCase(), null, "Imported " + imported + " " + catalogType + " items", null, request);
		return new AdminDtos.ImportResultResponse(catalogType, imported, List.of());
	}

	@Transactional(readOnly = true)
	public Object exportCatalog(String catalogType) {
		requireAdmin();
		return switch (catalogType) {
			case "expense-categories" -> listExpenseCategories();
			case "purchase-items" -> listPurchaseItems();
			case "meal-items" -> listMealItems();
			case "modes" -> listModes();
			case "recommendation-copy" -> listRecommendationCopy();
			case "settings" -> listSettings();
			default -> throw new IllegalArgumentException("Unsupported catalog type.");
		};
	}

	@Transactional(readOnly = true)
	public List<AdminDtos.AuditLogResponse> auditLog() {
		requireAdmin();
		return auditRepository.findTop100ByOrderByCreatedAtDesc().stream().map(this::toAuditLog).toList();
	}

	private void requireAdmin() {
		currentUserProvider.currentUserId();
	}

	private ResourceNotFoundException notFound(String label) {
		return new ResourceNotFoundException(label + " was not found");
	}

	private void apply(ExpenseCategoryEntity entity, AdminDtos.ExpenseCategoryRequest request) {
		entity.setCode(request.code());
		entity.setName(request.name());
		entity.setParentId(request.parentId());
		entity.setActive(request.active() == null || request.active());
		entity.setSortOrder(request.sortOrder() == null ? 100 : request.sortOrder());
	}

	private void apply(PurchaseCatalogItemEntity entity, AdminDtos.PurchaseItemRequest request) {
		entity.setCode(request.code());
		entity.setName(request.name());
		entity.setCategory(request.category());
		entity.setTier(request.tier().name());
		entity.setEstimatedMinPrice(request.estimatedMinPrice());
		entity.setEstimatedMaxPrice(request.estimatedMaxPrice());
		entity.setImpactLevel(request.impactLevel().name());
		entity.setUrgencyLevel(request.urgencyLevel().name());
		entity.setRecommendedMoment(request.recommendedMoment());
		entity.setEarlyPurchaseRisk(request.earlyPurchaseRisk());
		entity.setDependencies(request.dependencies());
		entity.setRationale(request.rationale());
		entity.setIsActive(request.active() == null || request.active());
		entity.setSortOrder(request.sortOrder() == null ? 100 : request.sortOrder());
	}

	private void apply(MealCatalogItemEntity entity, AdminDtos.MealItemRequest request) {
		entity.setCode(request.code());
		entity.setName(request.name());
		entity.setCategory(request.category());
		entity.setEstimatedCostMin(request.estimatedCostMin());
		entity.setEstimatedCostMax(request.estimatedCostMax());
		entity.setPrepTimeMinutes(request.prepTimeMinutes());
		entity.setEffortLevel(request.effortLevel());
		entity.setCravingLevel(request.cravingLevel());
		entity.setBudgetLevel(request.budgetLevel());
		entity.setRequiredEquipment(request.requiredEquipment());
		entity.setSuggestedMode(request.suggestedMode());
		entity.setDescription(request.description());
		entity.setActive(request.active() == null || request.active());
		entity.setSortOrder(request.sortOrder() == null ? 100 : request.sortOrder());
	}

	private void apply(AdaptiveModeEntity entity, AdminDtos.ModeRequest request) {
		entity.setName(request.name());
		entity.setDescription(request.description());
		entity.setObjective(request.objective());
		entity.setRecommendedMinDays(request.recommendedMinDays());
		entity.setRecommendedMaxDays(request.recommendedMaxDays());
		entity.setIntensityLevel(request.intensityLevel());
		entity.setSpendingPolicy(request.spendingPolicy());
		entity.setAlertPolicy(request.alertPolicy());
		entity.setPurchasePolicy(request.purchasePolicy());
		entity.setRoutinePolicy(request.routinePolicy());
		entity.setActive(request.active() == null || request.active());
		entity.setSortOrder(request.sortOrder() == null ? 100 : request.sortOrder());
	}

	private void apply(SystemSettingEntity entity, AdminDtos.SettingRequest request) {
		entity.setKey(request.key());
		entity.setValue(request.value());
		entity.setValueType(request.valueType());
		entity.setDescription(request.description());
		entity.setActive(request.active() == null || request.active());
	}

	private List<String> validateImport(String catalogType, AdminDtos.ImportRequest request) {
		List<String> errors = new ArrayList<>();
		if (request.items() == null || request.items().isEmpty()) {
			errors.add("Import payload must contain at least one item.");
		}
		if (!List.of("expense-categories", "purchase-items", "meal-items", "settings").contains(catalogType)) {
			errors.add("Unsupported catalog type.");
		}
		for (int i = 0; request.items() != null && i < request.items().size(); i++) {
			Map<String, Object> item = request.items().get(i);
			if (!item.containsKey("code") && !item.containsKey("key")) {
				errors.add("Item " + i + " is missing code/key.");
			}
			if (!item.containsKey("name") && !"settings".equals(catalogType)) {
				errors.add("Item " + i + " is missing name.");
			}
			if ("settings".equals(catalogType) && !item.containsKey("value")) {
				errors.add("Item " + i + " is missing value.");
			}
			try {
				if ("expense-categories".equals(catalogType)) {
					AdminDtos.ExpenseCategoryRequest parsed = objectMapper.convertValue(item, AdminDtos.ExpenseCategoryRequest.class);
					if (parsed.code() != null && expenseCategoryRepository.findByCodeIgnoreCase(parsed.code()).isPresent()) {
						errors.add("Item " + i + " uses an existing expense category code.");
					}
				} else if ("purchase-items".equals(catalogType)) {
					AdminDtos.PurchaseItemRequest parsed = objectMapper.convertValue(item, AdminDtos.PurchaseItemRequest.class);
					if (parsed.code() != null && purchaseRepository.findByCodeIgnoreCase(parsed.code()).isPresent()) {
						errors.add("Item " + i + " uses an existing purchase item code.");
					}
				} else if ("meal-items".equals(catalogType)) {
					AdminDtos.MealItemRequest parsed = objectMapper.convertValue(item, AdminDtos.MealItemRequest.class);
					if (parsed.code() != null && mealRepository.findByCodeIgnoreCase(parsed.code()).isPresent()) {
						errors.add("Item " + i + " uses an existing meal item code.");
					}
				} else if ("settings".equals(catalogType)) {
					AdminDtos.SettingRequest parsed = objectMapper.convertValue(item, AdminDtos.SettingRequest.class);
					if (parsed.key() != null && settingRepository.existsById(parsed.key())) {
						errors.add("Item " + i + " uses an existing setting key.");
					}
				}
			} catch (IllegalArgumentException exception) {
				errors.add("Item " + i + " has invalid field values.");
			}
		}
		return errors;
	}

	private void audit(String actionType, String entityType, Object entityId, String summary, String beforeJson, Object after) {
		AdminAuditLogEntity audit = new AdminAuditLogEntity();
		audit.setAdminUserId(currentUserProvider.currentUserId());
		audit.setActionType(actionType);
		audit.setEntityType(entityType);
		audit.setEntityId(entityId == null ? null : String.valueOf(entityId));
		audit.setSummary(summary);
		audit.setBeforeJson(beforeJson);
		audit.setAfterJson(json(after));
		auditRepository.save(audit);
	}

	private String json(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException exception) {
			return "{\"error\":\"Could not serialize admin snapshot\"}";
		}
	}

	private AdminDtos.ExpenseCategoryResponse toExpenseCategory(ExpenseCategoryEntity entity) {
		return new AdminDtos.ExpenseCategoryResponse(entity.getId(), entity.getCode(), entity.getName(), entity.getParentId(), entity.isActive(), entity.getSortOrder());
	}

	private AdminDtos.PurchaseItemResponse toPurchaseItem(PurchaseCatalogItemEntity entity) {
		return new AdminDtos.PurchaseItemResponse(
				entity.getId(), entity.getCode(), entity.getName(), entity.getCategory(), entity.getTier(),
				entity.getEstimatedMinPrice(), entity.getEstimatedMaxPrice(), entity.getImpactLevel(), entity.getUrgencyLevel(),
				entity.getRecommendedMoment(), entity.getEarlyPurchaseRisk(), entity.getDependencies(), entity.getRationale(),
				entity.getIsActive(), entity.getSortOrder());
	}

	private AdminDtos.MealItemResponse toMealItem(MealCatalogItemEntity entity) {
		return new AdminDtos.MealItemResponse(
				entity.getId(), entity.getCode(), entity.getName(), entity.getCategory(), entity.getEstimatedCostMin(),
				entity.getEstimatedCostMax(), entity.getPrepTimeMinutes(), entity.getEffortLevel(), entity.getCravingLevel(),
				entity.getBudgetLevel(), entity.getRequiredEquipment(), entity.getSuggestedMode(), entity.getDescription(),
				entity.isActive(), entity.getSortOrder());
	}

	private AdminDtos.ModeResponse toMode(AdaptiveModeEntity entity) {
		return new AdminDtos.ModeResponse(
				entity.getId(), entity.getCode(), entity.getName(), entity.getDescription(), entity.getObjective(),
				entity.getRecommendedMinDays(), entity.getRecommendedMaxDays(), entity.getIntensityLevel(), entity.getSpendingPolicy(),
				entity.getAlertPolicy(), entity.getPurchasePolicy(), entity.getRoutinePolicy(), entity.isActive(), entity.getSortOrder());
	}

	private AdminDtos.RecommendationCopyResponse toRecommendationCopy(RecommendationCopyEntity entity) {
		return new AdminDtos.RecommendationCopyResponse(entity.getRuleKey(), entity.getTitle(), entity.getMessage(), entity.getActionLabel(), entity.getSeverity(), entity.isActive());
	}

	private AdminDtos.SettingResponse toSetting(SystemSettingEntity entity) {
		return new AdminDtos.SettingResponse(entity.getKey(), entity.getValue(), entity.getValueType(), entity.getDescription(), entity.isActive());
	}

	private AdminDtos.AuditLogResponse toAuditLog(AdminAuditLogEntity entity) {
		return new AdminDtos.AuditLogResponse(
				entity.getId(), entity.getAdminUserId(), entity.getActionType(), entity.getEntityType(), entity.getEntityId(),
				entity.getSummary(), entity.getBeforeJson(), entity.getAfterJson(), entity.getCreatedAt());
	}
}
