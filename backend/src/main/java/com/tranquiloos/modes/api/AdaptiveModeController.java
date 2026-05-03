package com.tranquiloos.modes.api;

import java.util.List;

import com.tranquiloos.modes.application.AdaptiveModeService;
import com.tranquiloos.modes.application.ModeActivationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/modes")
public class AdaptiveModeController {

	private final AdaptiveModeService adaptiveModeService;
	private final ModeActivationService modeActivationService;

	public AdaptiveModeController(AdaptiveModeService adaptiveModeService, ModeActivationService modeActivationService) {
		this.adaptiveModeService = adaptiveModeService;
		this.modeActivationService = modeActivationService;
	}

	@GetMapping
	public List<AdaptiveModeResponse> listModes() {
		return adaptiveModeService.listModes();
	}

	@GetMapping("/active")
	public ActiveModeSummaryResponse activeMode() {
		return modeActivationService.activeMode();
	}

	@PostMapping("/activate")
	public ModeActivationResponse activate(@Valid @RequestBody ActivateModeRequest request) {
		return modeActivationService.activate(request);
	}

	@PostMapping("/active/end")
	public ActiveModeSummaryResponse endActive(@RequestBody(required = false) EndModeRequest request) {
		modeActivationService.endActive(request);
		return modeActivationService.activeMode();
	}

	@GetMapping("/history")
	public List<ModeActivationResponse> history(@RequestParam(required = false) Long scenarioId) {
		return modeActivationService.history(scenarioId);
	}
}
