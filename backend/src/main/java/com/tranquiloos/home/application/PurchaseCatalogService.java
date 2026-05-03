package com.tranquiloos.home.application;

import com.tranquiloos.home.api.PurchaseCatalogItemResponse;
import com.tranquiloos.home.infrastructure.PurchaseCatalogItemEntity;
import com.tranquiloos.home.infrastructure.PurchaseCatalogItemJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseCatalogService {

	private final PurchaseCatalogItemJpaRepository catalogRepository;

	public PurchaseCatalogService(PurchaseCatalogItemJpaRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	public List<PurchaseCatalogItemResponse> getCatalog(String tier, String category) {
		List<PurchaseCatalogItemEntity> items;

		if (tier != null && category != null) {
			items = catalogRepository.findByIsActiveAndTierAndCategoryOrderByTierAscSortOrderAscCategoryAscNameAsc(true,
					tier, category);
		} else if (tier != null) {
			items = catalogRepository.findByIsActiveAndTierOrderByTierAscSortOrderAscCategoryAscNameAsc(true, tier);
		} else if (category != null) {
			items = catalogRepository.findByIsActiveAndCategoryOrderByTierAscSortOrderAscCategoryAscNameAsc(true, category);
		} else {
			items = catalogRepository.findByIsActiveOrderByTierAscSortOrderAscCategoryAscNameAsc(true);
		}

		return items.stream().map(this::toResponse).toList();
	}

	public PurchaseCatalogItemEntity getCatalogItemById(Long id) {
		return catalogRepository.findById(id).orElse(null);
	}

	public List<PurchaseCatalogItemEntity> getAllActiveCatalogItems() {
		return catalogRepository.findByIsActiveOrderByTierAscSortOrderAscCategoryAscNameAsc(true);
	}

	private PurchaseCatalogItemResponse toResponse(PurchaseCatalogItemEntity entity) {
		return new PurchaseCatalogItemResponse(
				entity.getId(),
				entity.getCode(),
				entity.getName(),
				entity.getCategory(),
				entity.getTier(),
				entity.getEstimatedMinPrice(),
				entity.getEstimatedMaxPrice(),
				entity.getImpactLevel(),
				entity.getUrgencyLevel(),
				entity.getRecommendedMoment(),
				entity.getEarlyPurchaseRisk(),
				entity.getDependencies(),
				entity.getRationale(),
				entity.getIsActive(),
				entity.getSortOrder(),
				entity.getCreatedAt(),
				entity.getUpdatedAt());
	}
}
