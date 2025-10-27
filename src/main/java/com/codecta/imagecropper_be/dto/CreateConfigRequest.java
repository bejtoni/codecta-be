package com.codecta.imagecropper_be.dto;

import com.codecta.imagecropper_be.enums.LogoPosition;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateConfigRequest {

    @Min(value = 1, message = "scaleDown must be at least 1%")
    @Max(value = 25, message = "scaleDown cannot exceed 25%")
    private double scaleDown; // korisnik unosi npr. 5% = 0.05

    @NotNull
    private LogoPosition logoPosition;

    private MultipartFile logoImage; // može biti null
}
