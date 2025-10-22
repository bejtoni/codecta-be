package com.codecta.imagecropper_be.repository;

import com.codecta.imagecropper_be.entity.ImageConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageConfigRepository extends JpaRepository<ImageConfig, UUID> {}
