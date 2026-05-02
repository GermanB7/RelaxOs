package com.tranquiloos.recommendations.infrastructure;

import java.time.Instant;

import com.tranquiloos.recommendations.domain.RecommendationSeverity;
import com.tranquiloos.recommendations.domain.RecommendationStatus;
import com.tranquiloos.recommendations.domain.RecommendationType;
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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "recommendation")
public class RecommendationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "scenario_id")
	private Long scenarioId;

	@Column(name = "score_snapshot_id")
	private Long scoreSnapshotId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private RecommendationType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private RecommendationSeverity severity;

	@Column(nullable = false)
	private Integer priority;

	@Column(nullable = false, length = 160)
	private String title;

	@Column(nullable = false, columnDefinition = "text")
	private String message;

	@Column(name = "action_label", length = 120)
	private String actionLabel;

	@Column(name = "action_type", length = 50)
	private String actionType;

	@Column(name = "source_rule_key", length = 80)
	private String sourceRuleKey;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private RecommendationStatus status = RecommendationStatus.OPEN;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "context_json", columnDefinition = "jsonb")
	private String contextJson;

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
	public Long getUserId() { return userId; }
	public void setUserId(Long userId) { this.userId = userId; }
	public Long getScenarioId() { return scenarioId; }
	public void setScenarioId(Long scenarioId) { this.scenarioId = scenarioId; }
	public Long getScoreSnapshotId() { return scoreSnapshotId; }
	public void setScoreSnapshotId(Long scoreSnapshotId) { this.scoreSnapshotId = scoreSnapshotId; }
	public RecommendationType getType() { return type; }
	public void setType(RecommendationType type) { this.type = type; }
	public RecommendationSeverity getSeverity() { return severity; }
	public void setSeverity(RecommendationSeverity severity) { this.severity = severity; }
	public Integer getPriority() { return priority; }
	public void setPriority(Integer priority) { this.priority = priority; }
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	public String getActionLabel() { return actionLabel; }
	public void setActionLabel(String actionLabel) { this.actionLabel = actionLabel; }
	public String getActionType() { return actionType; }
	public void setActionType(String actionType) { this.actionType = actionType; }
	public String getSourceRuleKey() { return sourceRuleKey; }
	public void setSourceRuleKey(String sourceRuleKey) { this.sourceRuleKey = sourceRuleKey; }
	public RecommendationStatus getStatus() { return status; }
	public void setStatus(RecommendationStatus status) { this.status = status; }
	public String getContextJson() { return contextJson; }
	public void setContextJson(String contextJson) { this.contextJson = contextJson; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
}
