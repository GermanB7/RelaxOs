package com.tranquiloos.shared.error;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ApiErrorResponse handleValidation(MethodArgumentNotValidException exception) {
		List<String> details = exception.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.toList();
		return new ApiErrorResponse("VALIDATION_ERROR", "Invalid request", details);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ApiErrorResponse handleUnreadableRequest(HttpMessageNotReadableException exception) {
		return new ApiErrorResponse("INVALID_REQUEST", "Invalid request body", List.of(exception.getMostSpecificCause().getMessage()));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	ApiErrorResponse handleNotFound(ResourceNotFoundException exception) {
		return new ApiErrorResponse("RESOURCE_NOT_FOUND", exception.getMessage(), List.of());
	}

	@ExceptionHandler(ResourceConflictException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	ApiErrorResponse handleConflict(ResourceConflictException exception) {
		return new ApiErrorResponse("CONFLICT", exception.getMessage(), List.of());
	}

	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	ApiErrorResponse handleUnauthorized(UnauthorizedException exception) {
		return new ApiErrorResponse("UNAUTHORIZED", exception.getMessage(), List.of());
	}
}
