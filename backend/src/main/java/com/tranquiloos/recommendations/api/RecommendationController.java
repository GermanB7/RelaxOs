package com.tranquiloos.recommendations.api;

import com.tranquiloos.recommendations.application.GetRecommendationsService;
import com.tranquiloos.recommendations.application.RecommendationActionService;
import com.tranquiloos.recommendations.application.RecommendationEngineService;
import com.tranquiloos.recommendations.domain.RecommendationStatus;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class RecommendationController {

	private final GetRecommendationsService getRecommendationsService;
	private final RecommendationEngineService recommendationEngineService;
	private final RecommendationActionService recommendationActionService;

	public RecommendationController(
			GetRecommendationsService getRecommendationsService,
			RecommendationEngineService recommendationEngineService,
			RecommendationActionService recommendationActionService) {
		this.getRecommendationsService = getRecommendationsService;
		this.recommendationEngineService = recommendationEngineService;
		this.recommendationActionService = recommendationActionService;
	}

	@GetMapping("/recommendations")
	List<RecommendationResponse> listRecommendations(
			@RequestParam(required = false) RecommendationStatus status,
			@RequestParam(required = false) Long scenarioId) {
		return getRecommendationsService.listRecommendations(status, scenarioId);
	}

	@PostMapping("/recommendations/recalculate")
	RecalculateRecommendationsResponse recalculate(@RequestBody(required = false) RecalculateRecommendationsRequest request) {
		return recommendationEngineService.recalculate(request == null ? null : request.scenarioId());
	}

	@PostMapping("/recommendations/{id}/accept")
	RecommendationResponse accept(@PathVariable Long id, @RequestBody(required = false) RecommendationActionRequest request) {
		return recommendationActionService.accept(id, request);
	}

	@PostMapping("/recommendations/{id}/postpone")
	RecommendationResponse postpone(@PathVariable Long id, @RequestBody(required = false) RecommendationActionRequest request) {
		return recommendationActionService.postpone(id, request);
	}

	@PostMapping("/recommendations/{id}/dismiss")
	RecommendationResponse dismiss(@PathVariable Long id, @RequestBody(required = false) RecommendationActionRequest request) {
		return recommendationActionService.dismiss(id, request);
	}

}
