package com.codecta.imagecropper_be.dto;

import com.codecta.imagecropper_be.enums.LogoPosition;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigResponse {
    private UUID id;
    private double scaleDown;
    private LogoPosition logoPosition;
    private String logoPath;
}
