package com.tranquiloos.shared.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
class SystemStatusController {

	private final String appVersion;

	SystemStatusController(@Value("${app.version:0.0.1}") String appVersion) {
		this.appVersion = appVersion;
	}

	@GetMapping("/status")
	SystemStatusResponse status() {
		return new SystemStatusResponse("TranquiloOS", "UP", appVersion);
	}

	record SystemStatusResponse(String app, String status, String version) {
	}
}
