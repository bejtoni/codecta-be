package com.codecta.imagecropper_be.repository;

import com.codecta.imagecropper_be.entity.ImageConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImageConfigRepository extends JpaRepository<ImageConfig, UUID> {
}
