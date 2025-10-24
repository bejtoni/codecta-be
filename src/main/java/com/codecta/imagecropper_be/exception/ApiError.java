package com.codecta.imagecropper_be.exception;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ApiError {
    private Instant timestamp;
    private String path;
    private String errorCode;
    private String message;
}
