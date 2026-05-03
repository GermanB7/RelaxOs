package com.tranquiloos.home.infrastructure;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_purchase_item")
public class UserPurchaseItemEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(name = "scenario_id")
	private Long scenarioId;

	@Column(name = "catalog_item_id")
	private Long catalogItemId;

	@Column(nullable = false, length = 160)
	private String name;

	@Column(nullable = false, length = 80)
	private String category;

	@Column(nullable = false, length = 20)
	private String tier;

	@Column(name = "estimated_price", precision = 14, scale = 2)
	private BigDecimal estimatedPrice;

	@Column(name = "actual_price", precision = 14, scale = 2)
	private BigDecimal actualPrice;

	@Column(nullable = false, length = 30)
	private String status = "PENDING";

	@Column(nullable = false)
	private Integer priority = 100;

	@Column(columnDefinition = "TEXT")
	private String link;

	@Column(columnDefinition = "TEXT")
	private String notes;

	@Column(name = "purchased_at")
	private LocalDateTime purchasedAt;

	@Column(name = "postponed_until")
	private LocalDate postponedUntil;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	// Constructors
	public UserPurchaseItemEntity() {
	}

	public UserPurchaseItemEntity(Long userId, Long scenarioId, String name, String category, String tier) {
		this.userId = userId;
		this.scenarioId = scenarioId;
		this.name = name;
		this.category = category;
		this.tier = tier;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(Long scenarioId) {
		this.scenarioId = scenarioId;
	}

	public Long getCatalogItemId() {
		return catalogItemId;
	}

	public void setCatalogItemId(Long catalogItemId) {
		this.catalogItemId = catalogItemId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTier() {
		return tier;
	}

	public void setTier(String tier) {
		this.tier = tier;
	}

	public BigDecimal getEstimatedPrice() {
		return estimatedPrice;
	}

	public void setEstimatedPrice(BigDecimal estimatedPrice) {
		this.estimatedPrice = estimatedPrice;
	}

	public BigDecimal getActualPrice() {
		return actualPrice;
	}

	public void setActualPrice(BigDecimal actualPrice) {
		this.actualPrice = actualPrice;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public LocalDateTime getPurchasedAt() {
		return purchasedAt;
	}

	public void setPurchasedAt(LocalDateTime purchasedAt) {
		this.purchasedAt = purchasedAt;
	}

	public LocalDate getPostponedUntil() {
		return postponedUntil;
	}

	public void setPostponedUntil(LocalDate postponedUntil) {
		this.postponedUntil = postponedUntil;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
