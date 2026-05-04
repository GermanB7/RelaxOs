package com.tranquiloos.transport.infrastructure;

import java.math.BigDecimal;
import java.time.Instant;

import com.tranquiloos.transport.domain.TransportOptionType;
import com.tranquiloos.transport.domain.TransportRiskLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "transport_evaluation")
public class TransportEvaluationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "scenario_id", nullable = false)
	private Long scenarioId;

	@Enumerated(EnumType.STRING)
	@Column(name = "recommended_current_option", length = 40)
	private TransportOptionType recommendedCurrentOption;

	@Enumerated(EnumType.STRING)
	@Column(name = "future_viable_option", length = 40)
	private TransportOptionType futureViableOption;

	@Column(name = "transport_burden", precision = 8, scale = 6)
	private BigDecimal transportBurden;

	@Column(name = "fit_score", nullable = false)
	private Integer fitScore;

	@Enumerated(EnumType.STRING)
	@Column(name = "risk_level", nullable = false, length = 20)
	private TransportRiskLevel riskLevel;

	@Column(columnDefinition = "text")
	private String explanation;

	@Column(name = "conditions_to_switch", columnDefinition = "text")
	private String conditionsToSwitch;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "evaluated_options_json", columnDefinition = "jsonb")
	private String evaluatedOptionsJson;

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
	public TransportOptionType getRecommendedCurrentOption() { return recommendedCurrentOption; }
	public void setRecommendedCurrentOption(TransportOptionType recommendedCurrentOption) { this.recommendedCurrentOption = recommendedCurrentOption; }
	public TransportOptionType getFutureViableOption() { return futureViableOption; }
	public void setFutureViableOption(TransportOptionType futureViableOption) { this.futureViableOption = futureViableOption; }
	public BigDecimal getTransportBurden() { return transportBurden; }
	public void setTransportBurden(BigDecimal transportBurden) { this.transportBurden = transportBurden; }
	public Integer getFitScore() { return fitScore; }
	public void setFitScore(Integer fitScore) { this.fitScore = fitScore; }
	public TransportRiskLevel getRiskLevel() { return riskLevel; }
	public void setRiskLevel(TransportRiskLevel riskLevel) { this.riskLevel = riskLevel; }
	public String getExplanation() { return explanation; }
	public void setExplanation(String explanation) { this.explanation = explanation; }
	public String getConditionsToSwitch() { return conditionsToSwitch; }
	public void setConditionsToSwitch(String conditionsToSwitch) { this.conditionsToSwitch = conditionsToSwitch; }
	public String getEvaluatedOptionsJson() { return evaluatedOptionsJson; }
	public void setEvaluatedOptionsJson(String evaluatedOptionsJson) { this.evaluatedOptionsJson = evaluatedOptionsJson; }
	public Instant getCreatedAt() { return createdAt; }
}
