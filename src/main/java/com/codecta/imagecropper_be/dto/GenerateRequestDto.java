package com.codecta.imagecropper_be.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Metadata za /api/image/generate endpoint.
 * rect: obavezno - koordinate za crop u originalnim pikselima.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateRequestDto {

    @Valid
    private CropRectangleDto rect;
}
