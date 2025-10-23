package com.codecta.imagecropper_be.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Metadata za /api/image/preview endpoint.
 * Sadrži samo pravougaoni crop (u original pikselima).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreviewRequestDto {

    @NotNull(message = "rect is required")
    @Valid
    private CropRectangleDto rect;
}
