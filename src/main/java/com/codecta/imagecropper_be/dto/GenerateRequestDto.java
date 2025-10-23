package com.codecta.imagecropper_be.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Metadata za /api/image/generate endpoint.
 * rect: obavezno.
 * configId: opciono (ako postoji, primjenjuje se logo overlay iz tog configa).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateRequestDto {

    @Valid
    private CropRectangleDto rect;

    // Opcionalno; kada je zadato, očekujemo validan UUID string.
    private String configId;
}
