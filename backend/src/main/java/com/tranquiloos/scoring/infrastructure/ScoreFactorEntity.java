package com.tranquiloos.scoring.infrastructure;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "score_factor")
public class ScoreFactorEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "score_snapshot_id", nullable = false)
	private Long scoreSnapshotId;

	@Column(name = "factor_key", nullable = false, length = 80)
	private String factorKey;

	@Column(nullable = false, length = 120)
	private String label;

	@Column(name = "value_text", length = 120)
	private String valueText;

	@Column(nullable = false)
	private Integer impact;

	@Column(precision = 8, scale = 3)
	private BigDecimal weight;

	@Column(columnDefinition = "text")
	private String explanation;

	public Long getId() {
		return id;
	}

	public Long getScoreSnapshotId() {
		return scoreSnapshotId;
	}

	public void setScoreSnapshotId(Long scoreSnapshotId) {
		this.scoreSnapshotId = scoreSnapshotId;
	}

	public String getFactorKey() {
		return factorKey;
	}

	public void setFactorKey(String factorKey) {
		this.factorKey = factorKey;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValueText() {
		return valueText;
	}

	public void setValueText(String valueText) {
		this.valueText = valueText;
	}

	public Integer getImpact() {
		return impact;
	}

	public void setImpact(Integer impact) {
		this.impact = impact;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
}
