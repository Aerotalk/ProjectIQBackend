package com.grivetyglobals.invoiceiq.exception;

import com.grivetyglobals.invoiceiq.dto.error.ApiErrorResponse;
import com.grivetyglobals.invoiceiq.dto.error.ApiValidationError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LogManager.getLogger(this.getClass());

    /**
     * Handle validation errors from @Valid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("❌ [ERROR] Validation failed: {}", ex.getMessage());
        
        List<ApiValidationError> validationErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String field = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
                    Object rejectedValue = error instanceof FieldError ? ((FieldError) error).getRejectedValue() : null;
                    return new ApiValidationError(field, error.getDefaultMessage(), rejectedValue);
                })
                .collect(Collectors.toList());

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "Invalid input parameters",
                request
        );
        response.setValidationErrors(validationErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle our custom AppExceptions.
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiErrorResponse> handleAppException(AppException ex, WebRequest request) {
        log.warn("❌ [ERROR] AppException ({}): {}", ex.getStatus(), ex.getMessage());
        
        ApiErrorResponse response = buildErrorResponse(
                ex.getStatus(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                request
        );
        
        return new ResponseEntity<>(response, ex.getStatus());
    }
    
    /**
     * Handle Spring Security BadCredentials
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        log.warn("🛡️ [SECURITY] Bad credentials attempt: {}", ex.getMessage());
        
        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Authentication Failed",
                "Invalid email or password",
                request
        );
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Catch-all for unexpected RuntimeExceptions (prevents stack traces from leaking)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAllUncaughtException(Exception ex, WebRequest request) {
        log.error("❌ [ERROR] Uncaught exception: ", ex);
        
        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                request
        );
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ApiErrorResponse buildErrorResponse(HttpStatus status, String error, String message, WebRequest request) {
        String path = "";
        if (request instanceof ServletWebRequest) {
            path = ((ServletWebRequest) request).getRequest().getRequestURI();
        } else {
            path = request.getDescription(false).replace("uri=", "");
        }
        
        return ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(path)
                .build();
    }
}
