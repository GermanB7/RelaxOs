package com.tranquiloos.scoring.infrastructure;

import java.time.Instant;

import com.tranquiloos.scoring.domain.ConfidenceLevel;
import com.tranquiloos.scoring.domain.ScoreStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "score_snapshot")
public class ScoreSnapshotEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "scenario_id", nullable = false)
	private Long scenarioId;

	@Column(nullable = false)
	private Integer score;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private ScoreStatus status;

	@Enumerated(EnumType.STRING)
	@Column(name = "confidence_level", length = 20)
	private ConfidenceLevel confidenceLevel;

	@Column(columnDefinition = "text")
	private String summary;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "input_snapshot_json", columnDefinition = "jsonb")
	private String inputSnapshotJson;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@PrePersist
	void prePersist() {
		this.createdAt = Instant.now();
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

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public ScoreStatus getStatus() {
		return status;
	}

	public void setStatus(ScoreStatus status) {
		this.status = status;
	}

	public ConfidenceLevel getConfidenceLevel() {
		return confidenceLevel;
	}

	public void setConfidenceLevel(ConfidenceLevel confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}

	public String getSummary() {
		return summary;
	}

	public String getInputSnapshotJson() {
		return inputSnapshotJson;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setInputSnapshotJson(String inputSnapshotJson) {
		this.inputSnapshotJson = inputSnapshotJson;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
