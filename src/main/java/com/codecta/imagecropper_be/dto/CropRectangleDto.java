package com.codecta.imagecropper_be.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pravougaoni crop u ORIGINAL pikselima slike.
 * (0,0) je gornji lijevi ugao originalne slike.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropRectangleDto {

    @Min(value = 0, message = "x must be >= 0")
    private int x;

    @Min(value = 0, message = "y must be >= 0")
    private int y;

    @Positive(message = "width must be > 0")
    private int width;

    @Positive(message = "height must be > 0")
    private int height;
}
