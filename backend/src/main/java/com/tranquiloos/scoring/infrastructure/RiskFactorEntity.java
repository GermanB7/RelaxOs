package com.tranquiloos.scoring.infrastructure;

import com.tranquiloos.scoring.domain.RiskSeverity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "risk_factor")
public class RiskFactorEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "score_snapshot_id", nullable = false)
	private Long scoreSnapshotId;

	@Column(name = "risk_key", nullable = false, length = 80)
	private String riskKey;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private RiskSeverity severity;

	@Column(nullable = false, length = 160)
	private String title;

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

	public String getRiskKey() {
		return riskKey;
	}

	public void setRiskKey(String riskKey) {
		this.riskKey = riskKey;
	}

	public RiskSeverity getSeverity() {
		return severity;
	}

	public void setSeverity(RiskSeverity severity) {
		this.severity = severity;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
}
