package com.tranquiloos.modes.application;

import java.util.List;

import com.tranquiloos.modes.api.AdaptiveModeResponse;
import com.tranquiloos.modes.infrastructure.AdaptiveModeEntity;
import com.tranquiloos.modes.infrastructure.AdaptiveModeJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdaptiveModeService {

	private final AdaptiveModeJpaRepository adaptiveModeRepository;

	public AdaptiveModeService(AdaptiveModeJpaRepository adaptiveModeRepository) {
		this.adaptiveModeRepository = adaptiveModeRepository;
	}

	@Transactional(readOnly = true)
	public List<AdaptiveModeResponse> listModes() {
		return adaptiveModeRepository.findByActiveTrueOrderBySortOrderAsc()
				.stream()
				.map(this::toResponse)
				.toList();
	}

	AdaptiveModeResponse toResponse(AdaptiveModeEntity mode) {
		return new AdaptiveModeResponse(
				mode.getId(),
				mode.getCode(),
				mode.getName(),
				mode.getDescription(),
				mode.getObjective(),
				mode.getRecommendedMinDays(),
				mode.getRecommendedMaxDays(),
				mode.getIntensityLevel(),
				mode.getSpendingPolicy(),
				mode.getAlertPolicy(),
				mode.getPurchasePolicy(),
				mode.getRoutinePolicy(),
				mode.getSortOrder());
	}
}
