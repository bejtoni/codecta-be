package com.codecta.imagecropper_be.entity;

import com.codecta.imagecropper_be.enums.LogoPosition;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "image_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private double scaleDown;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogoPosition position;

    @Column(name = "logo_path")
    private String logoPath;

    @Column(name = "user_id")
    private UUID userId;
}
