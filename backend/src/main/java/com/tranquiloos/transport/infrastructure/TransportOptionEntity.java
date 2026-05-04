package com.tranquiloos.transport.infrastructure;

import java.math.BigDecimal;
import java.time.Instant;

import com.tranquiloos.transport.domain.TransportOptionType;
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
@Table(name = "transport_option")
public class TransportOptionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "scenario_id", nullable = false)
	private Long scenarioId;

	@Enumerated(EnumType.STRING)
	@Column(name = "option_type", nullable = false, length = 40)
	private TransportOptionType optionType;

	@Column(name = "monthly_cost", nullable = false, precision = 14, scale = 2)
	private BigDecimal monthlyCost;

	@Column(name = "trips_per_week", nullable = false)
	private Integer tripsPerWeek;

	@Column(name = "average_time_minutes", nullable = false)
	private Integer averageTimeMinutes;

	@Column(name = "comfort_score", nullable = false)
	private Integer comfortScore;

	@Column(name = "safety_score", nullable = false)
	private Integer safetyScore;

	@Column(name = "flexibility_score", nullable = false)
	private Integer flexibilityScore;

	@Column(name = "parking_cost", precision = 14, scale = 2)
	private BigDecimal parkingCost;

	@Column(name = "maintenance_cost", precision = 14, scale = 2)
	private BigDecimal maintenanceCost;

	@Column(name = "insurance_cost", precision = 14, scale = 2)
	private BigDecimal insuranceCost;

	@Column(name = "fuel_cost", precision = 14, scale = 2)
	private BigDecimal fuelCost;

	@Column(name = "upfront_cost", precision = 14, scale = 2)
	private BigDecimal upfrontCost;

	@Column(name = "has_parking")
	private Boolean hasParking;

	@Column(name = "has_license")
	private Boolean hasLicense;

	@Column(columnDefinition = "text")
	private String notes;

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
	public Long getScenarioId() { return scenarioId; }
	public void setScenarioId(Long scenarioId) { this.scenarioId = scenarioId; }
	public TransportOptionType getOptionType() { return optionType; }
	public void setOptionType(TransportOptionType optionType) { this.optionType = optionType; }
	public BigDecimal getMonthlyCost() { return monthlyCost; }
	public void setMonthlyCost(BigDecimal monthlyCost) { this.monthlyCost = monthlyCost; }
	public Integer getTripsPerWeek() { return tripsPerWeek; }
	public void setTripsPerWeek(Integer tripsPerWeek) { this.tripsPerWeek = tripsPerWeek; }
	public Integer getAverageTimeMinutes() { return averageTimeMinutes; }
	public void setAverageTimeMinutes(Integer averageTimeMinutes) { this.averageTimeMinutes = averageTimeMinutes; }
	public Integer getComfortScore() { return comfortScore; }
	public void setComfortScore(Integer comfortScore) { this.comfortScore = comfortScore; }
	public Integer getSafetyScore() { return safetyScore; }
	public void setSafetyScore(Integer safetyScore) { this.safetyScore = safetyScore; }
	public Integer getFlexibilityScore() { return flexibilityScore; }
	public void setFlexibilityScore(Integer flexibilityScore) { this.flexibilityScore = flexibilityScore; }
	public BigDecimal getParkingCost() { return parkingCost; }
	public void setParkingCost(BigDecimal parkingCost) { this.parkingCost = parkingCost; }
	public BigDecimal getMaintenanceCost() { return maintenanceCost; }
	public void setMaintenanceCost(BigDecimal maintenanceCost) { this.maintenanceCost = maintenanceCost; }
	public BigDecimal getInsuranceCost() { return insuranceCost; }
	public void setInsuranceCost(BigDecimal insuranceCost) { this.insuranceCost = insuranceCost; }
	public BigDecimal getFuelCost() { return fuelCost; }
	public void setFuelCost(BigDecimal fuelCost) { this.fuelCost = fuelCost; }
	public BigDecimal getUpfrontCost() { return upfrontCost; }
	public void setUpfrontCost(BigDecimal upfrontCost) { this.upfrontCost = upfrontCost; }
	public Boolean getHasParking() { return hasParking; }
	public void setHasParking(Boolean hasParking) { this.hasParking = hasParking; }
	public Boolean getHasLicense() { return hasLicense; }
	public void setHasLicense(Boolean hasLicense) { this.hasLicense = hasLicense; }
	public String getNotes() { return notes; }
	public void setNotes(String notes) { this.notes = notes; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
}
