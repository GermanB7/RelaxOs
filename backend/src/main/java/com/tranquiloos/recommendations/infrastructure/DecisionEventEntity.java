package com.tranquiloos.recommendations.infrastructure;

import java.time.Instant;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "decision_event")
public class DecisionEventEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "scenario_id")
	private Long scenarioId;

	@Column(name = "recommendation_id")
	private Long recommendationId;

	@Column(name = "decision_type", nullable = false, length = 50)
	private String decisionType;

	@Column(nullable = false, columnDefinition = "text")
	private String question;

	@Column(name = "chosen_option", length = 120)
	private String chosenOption;

	@Column(name = "score_before")
	private Integer scoreBefore;

	@Column(name = "score_after")
	private Integer scoreAfter;

	@Column(columnDefinition = "text")
	private String reason;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "context_json", columnDefinition = "jsonb")
	private String contextJson;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@PrePersist
	void prePersist() {
		this.createdAt = Instant.now();
	}

	public Long getId() { return id; }
	public Long getUserId() { return userId; }
	public void setUserId(Long userId) { this.userId = userId; }
	public Long getScenarioId() { return scenarioId; }
	public void setScenarioId(Long scenarioId) { this.scenarioId = scenarioId; }
	public Long getRecommendationId() { return recommendationId; }
	public void setRecommendationId(Long recommendationId) { this.recommendationId = recommendationId; }
	public String getDecisionType() { return decisionType; }
	public void setDecisionType(String decisionType) { this.decisionType = decisionType; }
	public String getQuestion() { return question; }
	public void setQuestion(String question) { this.question = question; }
	public String getChosenOption() { return chosenOption; }
	public void setChosenOption(String chosenOption) { this.chosenOption = chosenOption; }
	public Integer getScoreBefore() { return scoreBefore; }
	public void setScoreBefore(Integer scoreBefore) { this.scoreBefore = scoreBefore; }
	public Integer getScoreAfter() { return scoreAfter; }
	public void setScoreAfter(Integer scoreAfter) { this.scoreAfter = scoreAfter; }
	public String getReason() { return reason; }
	public void setReason(String reason) { this.reason = reason; }
	public String getContextJson() { return contextJson; }
	public void setContextJson(String contextJson) { this.contextJson = contextJson; }
	public Instant getCreatedAt() { return createdAt; }
}
