package com.tranquiloos.admin.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.tranquiloos.home.domain.ImpactLevel;
import com.tranquiloos.home.domain.PurchaseTier;
import com.tranquiloos.home.domain.UrgencyLevel;
import com.tranquiloos.meals.domain.BudgetLevel;
import com.tranquiloos.meals.domain.CravingLevel;
import com.tranquiloos.meals.domain.EffortLevel;
import com.tranquiloos.modes.domain.AlertPolicy;
import com.tranquiloos.modes.domain.IntensityLevel;
import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.modes.domain.PurchasePolicy;
import com.tranquiloos.modes.domain.RoutinePolicy;
import com.tranquiloos.modes.domain.SpendingPolicy;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public final class AdminDtos {

	private AdminDtos() {
	}

	public record OverviewResponse(long expenseCategories, long purchaseItems, long mealItems, long modes, long settings, long auditEvents) {
	}

	public record ExpenseCategoryRequest(
			@NotBlank @Size(max = 50) String code,
			@NotBlank @Size(max = 120) String name,
			Long parentId,
			Boolean active,
			@Min(0) Integer sortOrder) {
	}

	public record ExpenseCategoryResponse(Long id, String code, String name, Long parentId, boolean active, Integer sortOrder) {
	}

	public record PurchaseItemRequest(
			@NotBlank @Size(max = 80) String code,
			@NotBlank @Size(max = 160) String name,
			@NotBlank @Size(max = 80) String category,
			@NotNull PurchaseTier tier,
			@PositiveOrZero BigDecimal estimatedMinPrice,
			@PositiveOrZero BigDecimal estimatedMaxPrice,
			@NotNull ImpactLevel impactLevel,
			@NotNull UrgencyLevel urgencyLevel,
			@Size(max = 160) String recommendedMoment,
			@Size(max = 2000) String earlyPurchaseRisk,
			@Size(max = 2000) String dependencies,
			@Size(max = 2000) String rationale,
			Boolean active,
			@Min(0) Integer sortOrder) {
	}

	public record PurchaseItemResponse(
			Long id,
			String code,
			String name,
			String category,
			String tier,
			BigDecimal estimatedMinPrice,
			BigDecimal estimatedMaxPrice,
			String impactLevel,
			String urgencyLevel,
			String recommendedMoment,
			String earlyPurchaseRisk,
			String dependencies,
			String rationale,
			boolean active,
			Integer sortOrder) {
	}

	public record MealItemRequest(
			@NotBlank @Size(max = 80) String code,
			@NotBlank @Size(max = 160) String name,
			@NotBlank @Size(max = 80) String category,
			@PositiveOrZero BigDecimal estimatedCostMin,
			@PositiveOrZero BigDecimal estimatedCostMax,
			@NotNull @Min(0) Integer prepTimeMinutes,
			@NotNull EffortLevel effortLevel,
			@NotNull CravingLevel cravingLevel,
			@NotNull BudgetLevel budgetLevel,
			@Size(max = 2000) String requiredEquipment,
			ModeCode suggestedMode,
			@Size(max = 2000) String description,
			Boolean active,
			@Min(0) Integer sortOrder) {
	}

	public record MealItemResponse(
			Long id,
			String code,
			String name,
			String category,
			BigDecimal estimatedCostMin,
			BigDecimal estimatedCostMax,
			Integer prepTimeMinutes,
			EffortLevel effortLevel,
			CravingLevel cravingLevel,
			BudgetLevel budgetLevel,
			String requiredEquipment,
			ModeCode suggestedMode,
			String description,
			boolean active,
			Integer sortOrder) {
	}

	public record ModeRequest(
			@NotBlank @Size(max = 120) String name,
			@Size(max = 2000) String description,
			@Size(max = 2000) String objective,
			@Min(0) Integer recommendedMinDays,
			@Min(0) Integer recommendedMaxDays,
			@NotNull IntensityLevel intensityLevel,
			@NotNull SpendingPolicy spendingPolicy,
			@NotNull AlertPolicy alertPolicy,
			@NotNull PurchasePolicy purchasePolicy,
			@NotNull RoutinePolicy routinePolicy,
			Boolean active,
			@Min(0) Integer sortOrder) {
	}

	public record ModeResponse(
			Long id,
			ModeCode code,
			String name,
			String description,
			String objective,
			Integer recommendedMinDays,
			Integer recommendedMaxDays,
			IntensityLevel intensityLevel,
			SpendingPolicy spendingPolicy,
			AlertPolicy alertPolicy,
			PurchasePolicy purchasePolicy,
			RoutinePolicy routinePolicy,
			boolean active,
			Integer sortOrder) {
	}

	public record RecommendationCopyRequest(
			@NotBlank @Size(max = 160) String title,
			@NotBlank @Size(max = 4000) String message,
			@Size(max = 120) String actionLabel,
			@Size(max = 20) String severity,
			Boolean active) {
	}

	public record RecommendationCopyResponse(String ruleKey, String title, String message, String actionLabel, String severity, boolean active) {
	}

	public record SettingRequest(
			@NotBlank @Size(max = 120) String key,
			@NotBlank @Size(max = 500) String value,
			@NotBlank @Size(max = 30) String valueType,
			@Size(max = 2000) String description,
			Boolean active) {
	}

	public record SettingResponse(String key, String value, String valueType, String description, boolean active) {
	}

	public record ImportRequest(@NotNull List<Map<String, Object>> items) {
	}

	public record ImportResultResponse(String catalogType, int importedCount, List<String> errors) {
	}

	public record AuditLogResponse(
			Long id,
			Long adminUserId,
			String actionType,
			String entityType,
			String entityId,
			String summary,
			String beforeJson,
			String afterJson,
			Instant createdAt) {
	}
}
