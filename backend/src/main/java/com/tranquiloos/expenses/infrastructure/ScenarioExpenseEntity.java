package com.tranquiloos.expenses.infrastructure;

import java.math.BigDecimal;
import java.time.Instant;

import com.tranquiloos.expenses.domain.ExpenseFrequency;

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
@Table(name = "scenario_expense")
public class ScenarioExpenseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "scenario_id", nullable = false)
	private Long scenarioId;

	@Column(name = "category_id", nullable = false)
	private Long categoryId;

	@Column(nullable = false, length = 160)
	private String name;

	@Column(nullable = false, precision = 14, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private ExpenseFrequency frequency = ExpenseFrequency.MONTHLY;

	@Column(name = "is_essential", nullable = false)
	private boolean essential = true;

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

	public Long getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(Long scenarioId) {
		this.scenarioId = scenarioId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public ExpenseFrequency getFrequency() {
		return frequency;
	}

	public void setFrequency(ExpenseFrequency frequency) {
		this.frequency = frequency;
	}

	public boolean isEssential() {
		return essential;
	}

	public void setEssential(boolean essential) {
		this.essential = essential;
	}
}
