package com.tranquiloos.scoring.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tranquiloos.scoring.application.ScenarioScoreService;

@RestController
@RequestMapping("/api/v1/scenarios/{scenarioId}/score")
public class ScoreController {

	private final ScenarioScoreService scoreService;

	public ScoreController(ScenarioScoreService scoreService) {
		this.scoreService = scoreService;
	}

	@PostMapping("/calculate")
	ScoreResponse calculateScore(@PathVariable Long scenarioId) {
		return scoreService.calculateScore(scenarioId);
	}

	@GetMapping("/latest")
	ScoreResponse getLatestScore(@PathVariable Long scenarioId) {
		return scoreService.getLatestScore(scenarioId);
	}

	@GetMapping("/history")
	List<ScoreHistoryResponse> getScoreHistory(@PathVariable Long scenarioId) {
		return scoreService.getScoreHistory(scenarioId);
	}
}
