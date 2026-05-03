package com.tranquiloos.meals.infrastructure;

import java.math.BigDecimal;
import java.time.Instant;

import com.tranquiloos.meals.domain.BudgetLevel;
import com.tranquiloos.meals.domain.CravingLevel;
import com.tranquiloos.meals.domain.EffortLevel;
import com.tranquiloos.modes.domain.ModeCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "meal_catalog_item")
public class MealCatalogItemEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 80)
	private String code;

	@Column(nullable = false, length = 160)
	private String name;

	@Column(nullable = false, length = 80)
	private String category;

	@Column(name = "estimated_cost_min", precision = 14, scale = 2)
	private BigDecimal estimatedCostMin;

	@Column(name = "estimated_cost_max", precision = 14, scale = 2)
	private BigDecimal estimatedCostMax;

	@Column(name = "prep_time_minutes", nullable = false)
	private Integer prepTimeMinutes;

	@Enumerated(EnumType.STRING)
	@Column(name = "effort_level", nullable = false, length = 20)
	private EffortLevel effortLevel;

	@Enumerated(EnumType.STRING)
	@Column(name = "craving_level", nullable = false, length = 20)
	private CravingLevel cravingLevel;

	@Enumerated(EnumType.STRING)
	@Column(name = "budget_level", nullable = false, length = 20)
	private BudgetLevel budgetLevel;

	@Column(name = "required_equipment", columnDefinition = "text")
	private String requiredEquipment;

	@Enumerated(EnumType.STRING)
	@Column(name = "suggested_mode", length = 50)
	private ModeCode suggestedMode;

	@Column(columnDefinition = "text")
	private String description;

	@Column(name = "is_active", nullable = false)
	private boolean active;

	@Column(name = "sort_order", nullable = false)
	private Integer sortOrder;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	public Long getId() { return id; }
	public String getCode() { return code; }
	public String getName() { return name; }
	public String getCategory() { return category; }
	public BigDecimal getEstimatedCostMin() { return estimatedCostMin; }
	public BigDecimal getEstimatedCostMax() { return estimatedCostMax; }
	public Integer getPrepTimeMinutes() { return prepTimeMinutes; }
	public EffortLevel getEffortLevel() { return effortLevel; }
	public CravingLevel getCravingLevel() { return cravingLevel; }
	public BudgetLevel getBudgetLevel() { return budgetLevel; }
	public String getRequiredEquipment() { return requiredEquipment; }
	public ModeCode getSuggestedMode() { return suggestedMode; }
	public String getDescription() { return description; }
	public boolean isActive() { return active; }
	public Integer getSortOrder() { return sortOrder; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
}
