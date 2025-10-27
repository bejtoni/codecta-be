package com.codecta.imagecropper_be.dto;

import com.codecta.imagecropper_be.enums.LogoPosition;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfigRequest {
    @Min(value = 0, message = "scaleDown must be at least 0.01")
    @Max(value = 1, message = "scaleDown cannot exceed 0.25")
    private Double scaleDown;    // optional, normalizovana vrijednost (0.01-0.25)
    private LogoPosition position; // optional, enum koji se automatski parsira iz JSON stringa
    private MultipartFile logoImage; // optional
}
