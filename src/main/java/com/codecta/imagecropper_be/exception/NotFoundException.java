package com.codecta.imagecropper_be.exception;

/**
 * Custom izuzetak za slučajeve kada traženi entitet ne postoji u bazi.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
