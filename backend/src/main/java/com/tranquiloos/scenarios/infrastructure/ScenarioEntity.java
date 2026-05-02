package com.tranquiloos.scenarios.infrastructure;

import java.math.BigDecimal;
import java.time.Instant;

import com.tranquiloos.scenarios.domain.ScenarioStatus;

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
@Table(name = "scenario")
public class ScenarioEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(nullable = false, length = 160)
	private String name;

	@Column(name = "monthly_income", nullable = false, precision = 14, scale = 2)
	private BigDecimal monthlyIncome;

	@Column(name = "emergency_fund_current", nullable = false, precision = 14, scale = 2)
	private BigDecimal emergencyFundCurrent = BigDecimal.ZERO;

	@Column(name = "emergency_fund_target", precision = 14, scale = 2)
	private BigDecimal emergencyFundTarget;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private ScenarioStatus status = ScenarioStatus.DRAFT;

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

	public Long getId() {
		return id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getMonthlyIncome() {
		return monthlyIncome;
	}

	public void setMonthlyIncome(BigDecimal monthlyIncome) {
		this.monthlyIncome = monthlyIncome;
	}

	public BigDecimal getEmergencyFundCurrent() {
		return emergencyFundCurrent;
	}

	public void setEmergencyFundCurrent(BigDecimal emergencyFundCurrent) {
		this.emergencyFundCurrent = emergencyFundCurrent;
	}

	public BigDecimal getEmergencyFundTarget() {
		return emergencyFundTarget;
	}

	public void setEmergencyFundTarget(BigDecimal emergencyFundTarget) {
		this.emergencyFundTarget = emergencyFundTarget;
	}

	public ScenarioStatus getStatus() {
		return status;
	}

	public void setStatus(ScenarioStatus status) {
		this.status = status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}
}
