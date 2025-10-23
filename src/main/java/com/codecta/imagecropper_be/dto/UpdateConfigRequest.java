package com.codecta.imagecropper_be.dto;

import com.codecta.imagecropper_be.enums.LogoPosition;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfigRequest {
    private Double scaleDown;             // optional
    private LogoPosition logoPosition;    // optional
    private MultipartFile logoImage;      // optional
}
