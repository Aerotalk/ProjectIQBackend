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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LogManager.getLogger(this.getClass());

    // Handles our custom validation errors (like "Please verify your email")
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Access Denied");
        response.put("message", "You do not have the required permissions to perform this action.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
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
