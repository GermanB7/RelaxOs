package com.tranquiloos.home.application;

import com.tranquiloos.home.api.CreateCustomPurchaseItemRequest;
import com.tranquiloos.home.api.CreateHomeRoadmapRequest;
import com.tranquiloos.home.api.HomeSetupSummaryResponse;
import com.tranquiloos.home.api.HomeSetupSummaryResponse.NextBestPurchaseResponse;
import com.tranquiloos.home.api.PurchaseCatalogItemResponse;
import com.tranquiloos.home.api.UpdatePurchaseItemRequest;
import com.tranquiloos.home.api.UpdatePurchaseStatusRequest;
import com.tranquiloos.home.api.UserPurchaseItemResponse;
import com.tranquiloos.home.domain.PurchaseStatus;
import com.tranquiloos.home.infrastructure.PurchaseCatalogItemEntity;
import com.tranquiloos.home.infrastructure.UserPurchaseItemEntity;
import com.tranquiloos.home.infrastructure.UserPurchaseItemJpaRepository;
import com.tranquiloos.recommendations.infrastructure.DecisionEventEntity;
import com.tranquiloos.recommendations.infrastructure.DecisionEventJpaRepository;
import com.tranquiloos.scenarios.application.ScenarioService;
import com.tranquiloos.shared.error.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.decisions.domain.DecisionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HomeSetupService {

	private final UserPurchaseItemJpaRepository userPurchaseRepository;
	private final PurchaseCatalogService catalogService;
	private final HomeSetupPriorityService priorityService;
	private final DecisionEventJpaRepository decisionEventRepository;
	private final ScenarioService scenarioService;
	private final ObjectMapper objectMapper;

	public HomeSetupService(
			UserPurchaseItemJpaRepository userPurchaseRepository,
			PurchaseCatalogService catalogService,
			HomeSetupPriorityService priorityService,
			DecisionEventJpaRepository decisionEventRepository,
			ScenarioService scenarioService,
			ObjectMapper objectMapper) {
		this.userPurchaseRepository = userPurchaseRepository;
		this.catalogService = catalogService;
		this.priorityService = priorityService;
		this.decisionEventRepository = decisionEventRepository;
		this.scenarioService = scenarioService;
		this.objectMapper = objectMapper;
	}

	public List<PurchaseCatalogItemResponse> getCatalog(String tier, String category) {
		return catalogService.getCatalog(tier, category);
	}

	@Transactional
	public void initializeRoadmap(Long userId, CreateHomeRoadmapRequest request) {
		Long scenarioId = request.scenarioId();
		validateScenarioOwnership(scenarioId);
		List<PurchaseCatalogItemEntity> catalogItems = catalogService.getAllActiveCatalogItems();

		for (PurchaseCatalogItemEntity catalogItem : catalogItems) {
			// Check if item already exists for this user + scenario + catalog item
			if (scenarioId != null) {
				Optional<UserPurchaseItemEntity> existing = userPurchaseRepository
						.findByUserIdAndScenarioIdAndCatalogItemId(userId, scenarioId, catalogItem.getId());
				if (existing.isPresent()) {
					continue; // Skip if already exists
				}
			} else {
				Optional<UserPurchaseItemEntity> existing = userPurchaseRepository
						.findByUserIdAndCatalogItemIdAndScenarioIdIsNull(userId, catalogItem.getId());
				if (existing.isPresent()) {
					continue; // Skip if already exists
				}
			}

			// Create new user purchase item from catalog item
			UserPurchaseItemEntity newItem = new UserPurchaseItemEntity(
					userId,
					scenarioId,
					catalogItem.getName(),
					catalogItem.getCategory(),
					catalogItem.getTier());
			newItem.setCatalogItemId(catalogItem.getId());
			newItem.setEstimatedPrice(calculateMidpointPrice(catalogItem));
			newItem.setStatus(PurchaseStatus.PENDING.name());
			newItem.setPriority(priorityService.calculatePriority(
					catalogItem.getTier(),
					catalogItem.getImpactLevel(),
					catalogItem.getUrgencyLevel()));

			userPurchaseRepository.save(newItem);
		}
	}

	public List<UserPurchaseItemResponse> getRoadmapItems(Long userId, Long scenarioId, String status, String tier,
			String category) {
		List<UserPurchaseItemEntity> items;

		if (scenarioId != null) {
			items = userPurchaseRepository.findByUserIdAndScenarioIdOrderByPriorityAscTierAscStatusAscNameAsc(userId,
					scenarioId);
		} else {
			items = userPurchaseRepository.findByUserIdOrderByPriorityAscTierAscStatusAscNameAsc(userId);
		}

		// Filter by status, tier, category if provided
		items = items.stream()
				.filter(item -> status == null || item.getStatus().equals(status))
				.filter(item -> tier == null || item.getTier().equals(tier))
				.filter(item -> category == null || item.getCategory().equals(category))
				.toList();

		return items.stream().map(this::toResponse).toList();
	}

	public HomeSetupSummaryResponse getSummary(Long userId, Long scenarioId) {
		List<UserPurchaseItemEntity> items;

		if (scenarioId != null) {
			items = userPurchaseRepository.findByUserIdAndScenarioIdOrderByPriorityAscTierAscStatusAscNameAsc(userId,
					scenarioId);
		} else {
			items = userPurchaseRepository.findByUserIdOrderByPriorityAscTierAscStatusAscNameAsc(userId);
		}

		long totalItems = items.size();
		long pendingItems = items.stream().filter(i -> i.getStatus().equals(PurchaseStatus.PENDING.name())).count();
		long boughtItems = items.stream().filter(i -> i.getStatus().equals(PurchaseStatus.BOUGHT.name())).count();
		long postponedItems = items.stream()
				.filter(i -> i.getStatus().equals(PurchaseStatus.POSTPONED.name())).count();
		long wishlistItems = items.stream().filter(i -> i.getStatus().equals(PurchaseStatus.WISHLIST.name())).count();

		long tier1Total = items.stream().filter(i -> i.getTier().equals("TIER_1")).count();
		long tier1Bought = items.stream()
				.filter(i -> i.getTier().equals("TIER_1") && i.getStatus().equals(PurchaseStatus.BOUGHT.name()))
				.count();
		int tier1CompletionPercentage = tier1Total > 0 ? (int) ((tier1Bought * 100) / tier1Total) : 0;

		BigDecimal estimatedPendingCost = items.stream()
				.filter(i -> i.getStatus().equals(PurchaseStatus.PENDING.name()))
				.map(UserPurchaseItemEntity::getEstimatedPrice)
				.filter(price -> price != null)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		// Find next best purchase (lowest priority PENDING item in TIER_1)
		NextBestPurchaseResponse nextBestPurchase = items.stream()
				.filter(i -> i.getTier().equals("TIER_1") && i.getStatus().equals(PurchaseStatus.PENDING.name()))
				.min((a, b) -> Integer.compare(a.getPriority(), b.getPriority()))
				.map(item -> new NextBestPurchaseResponse(
						item.getId(),
						item.getName(),
						item.getTier(),
						item.getCategory(),
						item.getPriority()))
				.orElse(null);

		return new HomeSetupSummaryResponse(
				totalItems,
				pendingItems,
				boughtItems,
				postponedItems,
				wishlistItems,
				tier1Total,
				tier1Bought,
				tier1CompletionPercentage,
				estimatedPendingCost,
				nextBestPurchase);
	}

	@Transactional
	public UserPurchaseItemResponse createCustomItem(Long userId, CreateCustomPurchaseItemRequest request) {
		UserPurchaseItemEntity item = new UserPurchaseItemEntity(
				userId,
				request.scenarioId(),
				request.name(),
				request.category(),
				request.tier().name());
		validateScenarioOwnership(request.scenarioId());

		item.setEstimatedPrice(request.estimatedPrice());
		item.setLink(request.link());
		item.setNotes(request.notes());
		item.setStatus(PurchaseStatus.PENDING.name());

		if (request.priority() != null) {
			item.setPriority(request.priority());
		} else {
			item.setPriority(priorityService.calculateDefaultPriority(request.tier().name()));
		}

		UserPurchaseItemEntity saved = userPurchaseRepository.save(item);
		return toResponse(saved);
	}

	@Transactional
	public UserPurchaseItemResponse updateItem(Long userId, Long itemId, UpdatePurchaseItemRequest request) {
		UserPurchaseItemEntity item = userPurchaseRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("Purchase item not found"));

		if (!item.getUserId().equals(userId)) {
			throw new ResourceNotFoundException("Purchase item not found");
		}

		if (request.name() != null) {
			item.setName(request.name());
		}
		if (request.category() != null) {
			item.setCategory(request.category());
		}
		if (request.tier() != null) {
			item.setTier(request.tier().name());
		}
		if (request.estimatedPrice() != null) {
			item.setEstimatedPrice(request.estimatedPrice());
		}
		if (request.actualPrice() != null) {
			item.setActualPrice(request.actualPrice());
		}
		if (request.priority() != null) {
			item.setPriority(request.priority());
		}
		if (request.link() != null) {
			item.setLink(request.link());
		}
		if (request.notes() != null) {
			item.setNotes(request.notes());
		}

		UserPurchaseItemEntity updated = userPurchaseRepository.save(item);
		return toResponse(updated);
	}

	@Transactional
	public UserPurchaseItemResponse updateItemStatus(Long userId, Long itemId, UpdatePurchaseStatusRequest request) {
		UserPurchaseItemEntity item = userPurchaseRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("Purchase item not found"));

		if (!item.getUserId().equals(userId)) {
			throw new ResourceNotFoundException("Purchase item not found");
		}

		String previousStatus = item.getStatus();
		String newStatus = request.status().name();
		item.setStatus(newStatus);

		if (request.actualPrice() != null) {
			item.setActualPrice(request.actualPrice());
		}

		if (request.status() == PurchaseStatus.BOUGHT) {
			item.setPurchasedAt(LocalDateTime.now());
		}

		UserPurchaseItemEntity updated = userPurchaseRepository.save(item);

		// Create decision event
		DecisionEventEntity decisionEvent = new DecisionEventEntity();
		decisionEvent.setUserId(userId);
		decisionEvent.setScenarioId(item.getScenarioId());
		decisionEvent.setDecisionType(purchaseDecisionType(request.status()).name());
		decisionEvent.setQuestion(item.getName());
		decisionEvent.setChosenOption(newStatus);
		decisionEvent.setReason(request.reason());
		Map<String, Object> context = new HashMap<>();
		context.put("itemId", itemId);
		context.put("previousStatus", previousStatus);
		context.put("newStatus", newStatus);
		context.put("actualPrice", request.actualPrice());
		decisionEvent.setContextJson(toJson(context));

		decisionEventRepository.save(decisionEvent);

		return toResponse(updated);
	}

	@Transactional
	public void deleteCustomItem(Long userId, Long itemId) {
		UserPurchaseItemEntity item = userPurchaseRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("Purchase item not found"));

		if (!item.getUserId().equals(userId)) {
			throw new ResourceNotFoundException("Purchase item not found");
		}

		// Only allow deletion of custom items (no catalog_item_id)
		if (item.getCatalogItemId() != null) {
			throw new IllegalArgumentException("Cannot delete catalog items. Use SKIPPED or POSTPONED status instead.");
		}

		userPurchaseRepository.delete(item);
	}

	private UserPurchaseItemResponse toResponse(UserPurchaseItemEntity entity) {
		return new UserPurchaseItemResponse(
				entity.getId(),
				entity.getName(),
				entity.getCategory(),
				entity.getTier(),
				entity.getEstimatedPrice(),
				entity.getActualPrice(),
				entity.getStatus(),
				entity.getPriority(),
				entity.getLink(),
				entity.getNotes(),
				entity.getPurchasedAt(),
				entity.getPostponedUntil(),
				entity.getCreatedAt(),
				entity.getUpdatedAt());
	}

	private BigDecimal calculateMidpointPrice(PurchaseCatalogItemEntity catalogItem) {
		if (catalogItem.getEstimatedMinPrice() != null && catalogItem.getEstimatedMaxPrice() != null) {
			return catalogItem.getEstimatedMinPrice().add(catalogItem.getEstimatedMaxPrice())
					.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
		}
		return catalogItem.getEstimatedMinPrice() != null ? catalogItem.getEstimatedMinPrice()
				: catalogItem.getEstimatedMaxPrice();
	}

	private void validateScenarioOwnership(Long scenarioId) {
		if (scenarioId != null) {
			scenarioService.findCurrentUserScenario(scenarioId);
		}
	}

	private String toJson(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException exception) {
			return "{}";
		}
	}

	private DecisionType purchaseDecisionType(PurchaseStatus status) {
		return switch (status) {
			case BOUGHT -> DecisionType.PURCHASE_BOUGHT;
			case POSTPONED -> DecisionType.PURCHASE_POSTPONED;
			default -> DecisionType.PURCHASE_POSTPONED;
		};
	}
}
