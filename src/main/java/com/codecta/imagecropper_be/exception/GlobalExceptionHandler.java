package com.codecta.imagecropper_be.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> badRequest(IllegalArgumentException ex, HttpServletRequest req) {
        // Provjeri da li je greška vezana za PNG validaciju
        if (ex.getMessage().contains("image/png")) {
            return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", ex.getMessage(), req.getRequestURI());
        }
        // Provjeri da li je greška vezana za user config
        if (ex.getMessage().contains("User configuration not found")) {
            return build(HttpStatus.BAD_REQUEST, "MISSING_CONFIG", ex.getMessage(), req.getRequestURI());
        }
        // Provjeri da li je greška vezana za rectangle validaciju
        if (ex.getMessage().contains("Rectangle") || ex.getMessage().contains("coordinates") || ex.getMessage().contains("dimensions")) {
            return build(HttpStatus.BAD_REQUEST, "INVALID_RECT", ex.getMessage(), req.getRequestURI());
        }
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> notFound(NotFoundException ex, HttpServletRequest req) {
        // Provjeri da li je greška vezana za config
        if (ex.getMessage().contains("Config not found")) {
            return build(HttpStatus.NOT_FOUND, "CONFIG_NOT_FOUND", ex.getMessage(), req.getRequestURI());
        }
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> other(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", ex.getMessage(), req.getRequestURI());
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String code, String msg, String path) {
        return ResponseEntity.status(status).body(
                ApiError.builder()
                        .timestamp(Instant.now())
                        .errorCode(code)
                        .message(msg)
                        .path(path)
                        .build()
        );
    }
}
