package com.tranquiloos.home.infrastructure;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_catalog_item")
public class PurchaseCatalogItemEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 80)
	private String code;

	@Column(nullable = false, length = 160)
	private String name;

	@Column(nullable = false, length = 80)
	private String category;

	@Column(nullable = false, length = 20)
	private String tier;

	@Column(name = "estimated_min_price", precision = 14, scale = 2)
	private BigDecimal estimatedMinPrice;

	@Column(name = "estimated_max_price", precision = 14, scale = 2)
	private BigDecimal estimatedMaxPrice;

	@Column(nullable = false, length = 20)
	private String impactLevel;

	@Column(nullable = false, length = 20)
	private String urgencyLevel;

	@Column(length = 160)
	private String recommendedMoment;

	@Column(columnDefinition = "TEXT")
	private String earlyPurchaseRisk;

	@Column(columnDefinition = "TEXT")
	private String dependencies;

	@Column(columnDefinition = "TEXT")
	private String rationale;

	@Column(nullable = false)
	private Boolean isActive = true;

	@Column(nullable = false)
	private Integer sortOrder = 100;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	// Constructors
	public PurchaseCatalogItemEntity() {
	}

	public PurchaseCatalogItemEntity(String code, String name, String category, String tier, String impactLevel,
			String urgencyLevel) {
		this.code = code;
		this.name = name;
		this.category = category;
		this.tier = tier;
		this.impactLevel = impactLevel;
		this.urgencyLevel = urgencyLevel;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public BigDecimal getEstimatedMinPrice() {
		return estimatedMinPrice;
	}

	public void setEstimatedMinPrice(BigDecimal estimatedMinPrice) {
		this.estimatedMinPrice = estimatedMinPrice;
	}

	public BigDecimal getEstimatedMaxPrice() {
		return estimatedMaxPrice;
	}

	public void setEstimatedMaxPrice(BigDecimal estimatedMaxPrice) {
		this.estimatedMaxPrice = estimatedMaxPrice;
	}

	public String getImpactLevel() {
		return impactLevel;
	}

	public void setImpactLevel(String impactLevel) {
		this.impactLevel = impactLevel;
	}

	public String getUrgencyLevel() {
		return urgencyLevel;
	}

	public void setUrgencyLevel(String urgencyLevel) {
		this.urgencyLevel = urgencyLevel;
	}

	public String getRecommendedMoment() {
		return recommendedMoment;
	}

	public void setRecommendedMoment(String recommendedMoment) {
		this.recommendedMoment = recommendedMoment;
	}

	public String getEarlyPurchaseRisk() {
		return earlyPurchaseRisk;
	}

	public void setEarlyPurchaseRisk(String earlyPurchaseRisk) {
		this.earlyPurchaseRisk = earlyPurchaseRisk;
	}

	public String getDependencies() {
		return dependencies;
	}

	public void setDependencies(String dependencies) {
		this.dependencies = dependencies;
	}

	public String getRationale() {
		return rationale;
	}

	public void setRationale(String rationale) {
		this.rationale = rationale;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
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
