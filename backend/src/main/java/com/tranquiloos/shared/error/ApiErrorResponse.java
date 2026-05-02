package com.tranquiloos.shared.error;

import java.util.List;

public record ApiErrorResponse(String code, String message, List<String> details) {
}
