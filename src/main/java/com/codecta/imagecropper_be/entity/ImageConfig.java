package com.codecta.imagecropper_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "image_config")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ImageConfig {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long UUID;

    // npr. 0.05f za preview, ili max 0.25f po zadatku
    private Float scaleDown;

    // TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER
    @Column(length = 32)
    private String logoPosition;

    @Lob
    @Column(columnDefinition = "VARBINARY(MAX)")
    private byte[] logoPng;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = createdAt;
    }
    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
