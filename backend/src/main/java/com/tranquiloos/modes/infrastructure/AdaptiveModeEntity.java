package com.tranquiloos.modes.infrastructure;

import java.time.Instant;

import com.tranquiloos.modes.domain.AlertPolicy;
import com.tranquiloos.modes.domain.IntensityLevel;
import com.tranquiloos.modes.domain.ModeCode;
import com.tranquiloos.modes.domain.PurchasePolicy;
import com.tranquiloos.modes.domain.RoutinePolicy;
import com.tranquiloos.modes.domain.SpendingPolicy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "adaptive_mode")
public class AdaptiveModeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, unique = true, length = 50)
	private ModeCode code;

	@Column(nullable = false, length = 120)
	private String name;

	@Column(columnDefinition = "text")
	private String description;

	@Column(columnDefinition = "text")
	private String objective;

	@Column(name = "recommended_min_days")
	private Integer recommendedMinDays;

	@Column(name = "recommended_max_days")
	private Integer recommendedMaxDays;

	@Enumerated(EnumType.STRING)
	@Column(name = "intensity_level", nullable = false, length = 20)
	private IntensityLevel intensityLevel;

	@Enumerated(EnumType.STRING)
	@Column(name = "spending_policy", nullable = false, length = 40)
	private SpendingPolicy spendingPolicy;

	@Enumerated(EnumType.STRING)
	@Column(name = "alert_policy", nullable = false, length = 40)
	private AlertPolicy alertPolicy;

	@Enumerated(EnumType.STRING)
	@Column(name = "purchase_policy", nullable = false, length = 40)
	private PurchasePolicy purchasePolicy;

	@Enumerated(EnumType.STRING)
	@Column(name = "routine_policy", nullable = false, length = 40)
	private RoutinePolicy routinePolicy;

	@Column(name = "is_active", nullable = false)
	private boolean active;

	@Column(name = "sort_order", nullable = false)
	private Integer sortOrder;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	public Long getId() { return id; }
	public ModeCode getCode() { return code; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	public String getObjective() { return objective; }
	public Integer getRecommendedMinDays() { return recommendedMinDays; }
	public Integer getRecommendedMaxDays() { return recommendedMaxDays; }
	public IntensityLevel getIntensityLevel() { return intensityLevel; }
	public SpendingPolicy getSpendingPolicy() { return spendingPolicy; }
	public AlertPolicy getAlertPolicy() { return alertPolicy; }
	public PurchasePolicy getPurchasePolicy() { return purchasePolicy; }
	public RoutinePolicy getRoutinePolicy() { return routinePolicy; }
	public boolean isActive() { return active; }
	public Integer getSortOrder() { return sortOrder; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
}
