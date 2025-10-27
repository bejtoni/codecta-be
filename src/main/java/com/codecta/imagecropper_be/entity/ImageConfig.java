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

    @Column(name = "scale_down_percent", nullable = false)
    private Double scaleDownPercent; // normalizovana vrijednost (0.01-0.25)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogoPosition position;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Lob
    @Column(name = "logo_blob")
    private byte[] logoBlob; // čuvamo samo bajtove loga
}
