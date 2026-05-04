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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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

	@PrePersist
	void prePersist() {
		Instant now = Instant.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	void preUpdate() {
		this.updatedAt = Instant.now();
	}

	public Long getId() { return id; }
	public String getCode() { return code; }
	public void setCode(String code) { this.code = code; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }
	public BigDecimal getEstimatedCostMin() { return estimatedCostMin; }
	public void setEstimatedCostMin(BigDecimal estimatedCostMin) { this.estimatedCostMin = estimatedCostMin; }
	public BigDecimal getEstimatedCostMax() { return estimatedCostMax; }
	public void setEstimatedCostMax(BigDecimal estimatedCostMax) { this.estimatedCostMax = estimatedCostMax; }
	public Integer getPrepTimeMinutes() { return prepTimeMinutes; }
	public void setPrepTimeMinutes(Integer prepTimeMinutes) { this.prepTimeMinutes = prepTimeMinutes; }
	public EffortLevel getEffortLevel() { return effortLevel; }
	public void setEffortLevel(EffortLevel effortLevel) { this.effortLevel = effortLevel; }
	public CravingLevel getCravingLevel() { return cravingLevel; }
	public void setCravingLevel(CravingLevel cravingLevel) { this.cravingLevel = cravingLevel; }
	public BudgetLevel getBudgetLevel() { return budgetLevel; }
	public void setBudgetLevel(BudgetLevel budgetLevel) { this.budgetLevel = budgetLevel; }
	public String getRequiredEquipment() { return requiredEquipment; }
	public void setRequiredEquipment(String requiredEquipment) { this.requiredEquipment = requiredEquipment; }
	public ModeCode getSuggestedMode() { return suggestedMode; }
	public void setSuggestedMode(ModeCode suggestedMode) { this.suggestedMode = suggestedMode; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }
	public Integer getSortOrder() { return sortOrder; }
	public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
}
