package com.tranquiloos.home.api;

import com.tranquiloos.home.application.HomeSetupService;
import com.tranquiloos.users.application.CurrentUserProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Home Setup Roadmap API Controller.
 * Handles catalog browsing and personal roadmap management.
 */
@RestController
@RequestMapping("/api/v1/home")
public class HomeSetupController {

	private final HomeSetupService homeSetupService;
	private final CurrentUserProvider currentUserProvider;

	public HomeSetupController(HomeSetupService homeSetupService, CurrentUserProvider currentUserProvider) {
		this.homeSetupService = homeSetupService;
		this.currentUserProvider = currentUserProvider;
	}

	/**
	 * GET /api/v1/home/catalog
	 * List base purchase catalog.
	 * Query params: tier, category
	 */
	@GetMapping("/catalog")
	public ResponseEntity<List<PurchaseCatalogItemResponse>> getCatalog(
			@RequestParam(required = false) String tier,
			@RequestParam(required = false) String category) {
		List<PurchaseCatalogItemResponse> items = homeSetupService.getCatalog(tier, category);
		return ResponseEntity.ok(items);
	}

	/**
	 * POST /api/v1/home/roadmap/initialize
	 * Initialize roadmap from catalog for a user and optional scenario.
	 */
	@PostMapping("/roadmap/initialize")
	public ResponseEntity<Void> initializeRoadmap(@Valid @RequestBody CreateHomeRoadmapRequest request) {
		Long userId = currentUserProvider.currentUserId();
		homeSetupService.initializeRoadmap(userId, request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * GET /api/v1/home/roadmap
	 * List user's personal purchase items.
	 * Query params: scenarioId, status, tier, category
	 */
	@GetMapping("/roadmap")
	public ResponseEntity<List<UserPurchaseItemResponse>> getRoadmap(
			@RequestParam(required = false) Long scenarioId,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String tier,
			@RequestParam(required = false) String category) {
		Long userId = currentUserProvider.currentUserId();
		List<UserPurchaseItemResponse> items = homeSetupService.getRoadmapItems(userId, scenarioId, status, tier,
				category);
		return ResponseEntity.ok(items);
	}

	/**
	 * GET /api/v1/home/roadmap/summary
	 * Get summary statistics of purchase roadmap.
	 * Query params: scenarioId
	 */
	@GetMapping("/roadmap/summary")
	public ResponseEntity<HomeSetupSummaryResponse> getSummary(@RequestParam(required = false) Long scenarioId) {
		Long userId = currentUserProvider.currentUserId();
		HomeSetupSummaryResponse summary = homeSetupService.getSummary(userId, scenarioId);
		return ResponseEntity.ok(summary);
	}

	/**
	 * POST /api/v1/home/roadmap/items
	 * Create custom purchase item (not from catalog).
	 */
	@PostMapping("/roadmap/items")
	public ResponseEntity<UserPurchaseItemResponse> createCustomItem(
			@Valid @RequestBody CreateCustomPurchaseItemRequest request) {
		Long userId = currentUserProvider.currentUserId();
		UserPurchaseItemResponse response = homeSetupService.createCustomItem(userId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * PUT /api/v1/home/roadmap/items/{id}
	 * Update purchase item details.
	 */
	@PutMapping("/roadmap/items/{id}")
	public ResponseEntity<UserPurchaseItemResponse> updateItem(@PathVariable Long id,
			@Valid @RequestBody UpdatePurchaseItemRequest request) {
		Long userId = currentUserProvider.currentUserId();
		UserPurchaseItemResponse response = homeSetupService.updateItem(userId, id, request);
		return ResponseEntity.ok(response);
	}

	/**
	 * PATCH /api/v1/home/roadmap/items/{id}/status
	 * Update purchase item status (BOUGHT, POSTPONED, WISHLIST, PENDING, SKIPPED).
	 * Creates decision event on status change.
	 */
	@PatchMapping("/roadmap/items/{id}/status")
	public ResponseEntity<UserPurchaseItemResponse> updateItemStatus(@PathVariable Long id,
			@Valid @RequestBody UpdatePurchaseStatusRequest request) {
		Long userId = currentUserProvider.currentUserId();
		UserPurchaseItemResponse response = homeSetupService.updateItemStatus(userId, id, request);
		return ResponseEntity.ok(response);
	}

	/**
	 * DELETE /api/v1/home/roadmap/items/{id}
	 * Delete custom purchase item.
	 * Can only delete custom items (not from catalog).
	 */
	@DeleteMapping("/roadmap/items/{id}")
	public ResponseEntity<Void> deleteCustomItem(@PathVariable Long id) {
		Long userId = currentUserProvider.currentUserId();
		homeSetupService.deleteCustomItem(userId, id);
		return ResponseEntity.noContent().build();
	}
}
