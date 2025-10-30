package com.codecta.imagecropper_be.service;

import com.codecta.imagecropper_be.dto.*;
import com.codecta.imagecropper_be.entity.ImageConfig;
import com.codecta.imagecropper_be.enums.LogoPosition;
import com.codecta.imagecropper_be.exception.NotFoundException;
import com.codecta.imagecropper_be.repository.ImageConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ImageConfigRepository repo;
    private static final long MAX_LOGO_SIZE = 5 * 1024 * 1024; // 5 MB

    /**
     * Kreira novu konfiguraciju za korisnika (samo kreiranje, bez ažuriranja)
     * @param userId UUID trenutnog korisnika (iz JWT-a)
     * @param scaleDownPercent vrijednost između 1-25 (sa FE slidera)
     * @param position string koji se mapira na LogoPosition enum
     * @return ConfigResponse sa normalizovanim podacima
     * @throws IllegalArgumentException ako config već postoji ili ako logo nije poslan
     */
    @Transactional
    public ConfigResponse createConfig(UUID userId, double scaleDownPercent, String position, MultipartFile logo) throws IOException {
        // Proveri da li config već postoji
        if (repo.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Config already exists for this user. Use PUT to update.");
        }

        // Validacija ulaza - logo je obavezan
        if (logo == null || logo.isEmpty()) {
            throw new IllegalArgumentException("Logo is required for creating configuration");
        }

        validateScaleDownPercent(scaleDownPercent);
        LogoPosition logoPosition = validateAndParsePosition(position);
        byte[] logoBlob = validateAndProcessLogo(logo);

        // Kreiraj novi config
        ImageConfig newConfig = ImageConfig.builder()
                .userId(userId)
                .scaleDownPercent(scaleDownPercent)
                .position(logoPosition)
                .logoBlob(logoBlob)
                .build();

        ImageConfig savedConfig = repo.save(newConfig);
        return toResponse(savedConfig);
    }

    /**
     * Dohvata konfiguraciju za korisnika
     * @param userId UUID korisnika
     * @return ConfigResponse ili NotFoundException ako ne postoji
     */
    public ConfigResponse getConfigByUserId(UUID userId) {
        ImageConfig config = repo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Config not found for user"));
        return toResponse(config);
    }

    /**
     * Ažurira postojeći config (parcijalno - samo poslana polja se mijenjaju)
     * @param userId UUID korisnika
     * @param scaleDown nova vrijednost scaleDown (opciono)
     * @param position nova pozicija (opciono)
     * @param logo novi logo (opciono)
     */
    @Transactional
    public ConfigResponse updateConfig(UUID userId, Double scaleDown, LogoPosition position, MultipartFile logo) throws IOException {
        ImageConfig existingConfig = repo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Config not found for user"));

        // Ažuriraj samo ako je vrijednost poslana
        if (scaleDown != null) {
            validateScaleDownPercent(scaleDown);
            existingConfig.setScaleDownPercent(scaleDown);
        }

        if (position != null) {
            existingConfig.setPosition(position);
        }

        // Logo mijenjamo samo ako je novi fajl poslan
        byte[] logoBlob = validateAndProcessLogo(logo);
        if (logoBlob != null) {
            existingConfig.setLogoBlob(logoBlob);
        }

        ImageConfig savedConfig = repo.save(existingConfig);
        return toResponse(savedConfig);
    }

    // --------------------- PRIVATE HELPERS -----------------------

    /**
     * Validira scaleDownPercent da je u rasponu [0.01, 0.25] (normalizovane vrijednosti)
     */
    private void validateScaleDownPercent(double scaleDownPercent) {
        if (scaleDownPercent < 0.01 || scaleDownPercent > 0.25) {
            throw new IllegalArgumentException("scaleDownPercent must be between 0.01 and 0.25 (1% and 25%)");
        }
    }

    /**
     * Validira i parsira position string u LogoPosition enum
     */
    private LogoPosition validateAndParsePosition(String position) {
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException("Position cannot be null or empty");
        }
        
        try {
            return LogoPosition.valueOf(position.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid position: " + position + ". Valid values: " + 
                    java.util.Arrays.toString(LogoPosition.values()));
        }
    }

    /**
     * Validira i procesira logo fajl
     * @param logo MultipartFile
     * @return byte[] sa sadržajem loga ili null ako nije poslan
     */
    private byte[] validateAndProcessLogo(MultipartFile logo) throws IOException {
        if (logo == null || logo.isEmpty()) {
            return null;
        }

        // Provjeri Content-Type
        if (!"image/png".equalsIgnoreCase(logo.getContentType())) {
            throw new IllegalArgumentException("Logo must be image/png");
        }

        // Provjeri veličinu
        if (logo.getSize() > MAX_LOGO_SIZE) {
            throw new IllegalArgumentException("Logo size must be ≤ 5 MB");
        }

        return logo.getBytes();
    }

    /**
     * Pretvara entitet u DTO za response
     */
    private ConfigResponse toResponse(ImageConfig config) {
        return ConfigResponse.builder()
                .id(config.getId())
                .scaleDown(config.getScaleDownPercent()) // vraćamo normalizovanu vrijednost (0.01-0.25)
                .logoPosition(config.getPosition().name()) // vraćamo string
                .hasLogo(config.getLogoBlob() != null && config.getLogoBlob().length > 0)
                .build();
    }
}
