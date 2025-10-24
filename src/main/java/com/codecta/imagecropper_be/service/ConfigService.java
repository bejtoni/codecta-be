package com.codecta.imagecropper_be.service;

import com.codecta.imagecropper_be.dto.*;
import com.codecta.imagecropper_be.entity.ImageConfig;
import com.codecta.imagecropper_be.exception.NotFoundException;
import com.codecta.imagecropper_be.repository.ImageConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ImageConfigRepository repo; // JPA repository za ImageConfig entitet
    private static final String LOGO_DIR = "./data/logos/"; // folder gdje se čuvaju logo slike

    /**
     * Kreira novi config (sa scaleDown, pozicijom i opcionim logom)
     */
    public ConfigResponse create(CreateConfigRequest req) throws IOException {
        validateScale(req.getScaleDown()); // provjeri da je scaleDown između 1 i 25 (%)
        String logoPath = saveLogoIfPresent(req.getLogoImage()); // ako postoji logo, snimi ga na disk

        // Izgradi novi entitet i konvertuj scaleDown iz % u decimalni faktor (npr. 5% -> 0.05)
        ImageConfig cfg = ImageConfig.builder()
                .scaleDown(req.getScaleDown() / 100.0)
                .position(req.getLogoPosition())
                .logoPath(logoPath)
                .build();

        repo.save(cfg); // sačuvaj u bazu
        return toResponse(cfg); // pretvori u DTO za response
    }

    /**
     * Ažurira postojeći config (djelimično)
     */
    public ConfigResponse update(UUID id, UpdateConfigRequest req) throws IOException {
        // Dohvati postojeći config ili baci grešku ako ne postoji
        ImageConfig cfg = repo.findById(id).orElseThrow(() ->
                new NotFoundException("Config not found with"));

        // Ako je došao novi scaleDown -> validiraj i postavi
        if (req.getScaleDown() != null) {
            validateScale(req.getScaleDown());
            cfg.setScaleDown(req.getScaleDown() / 100.0);
        }

        // Ako je došla nova pozicija loga -> postavi
        if (req.getLogoPosition() != null) {
            cfg.setPosition(req.getLogoPosition());
        }

        // Ako je stigao novi logo fajl -> snimi ga na disk i ažuriraj path
        if (req.getLogoImage() != null && !req.getLogoImage().isEmpty()) {
            cfg.setLogoPath(saveLogoIfPresent(req.getLogoImage()));
        }

        repo.save(cfg); // spremi promjene u bazu
        return toResponse(cfg);
    }

    /**
     * Dohvata jedan config po ID-u
     */
    public ConfigResponse get(UUID id) {
        ImageConfig cfg = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Config not found"));
        return toResponse(cfg);
    }

    // --------------------- PRIVATE HELPERS -----------------------

    /**
     * Validira da je scaleDown u dozvoljenom rasponu [1%, 25%].
     */
    private void validateScale(double s) {
        if (s < 1 || s > 25)
            throw new IllegalArgumentException("scaleDown must be between 1% and 25%");
    }

    /**
     * Ako postoji uploadovani logo, snima ga u ./data/logos/ i vraća apsolutni path.
     * Ako logo nije poslan -> vraća null.
     */
    private String saveLogoIfPresent(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        // Ograniči tip fajla samo na PNG
        if (!"image/png".equalsIgnoreCase(file.getContentType()))
            throw new IllegalArgumentException("Logo must be image/png");

        // Kreiraj folder ako ne postoji
        File dir = new File(LOGO_DIR);
        if (!dir.exists()) dir.mkdirs();

        // Generiši jedinstveno ime fajla
        String filename = UUID.randomUUID() + "-" + StringUtils.cleanPath(file.getOriginalFilename());
        File dest = new File(dir, filename);

        // Snimi fajl
        file.transferTo(dest);

        // Vrati apsolutni path koji se čuva u bazi
        return dest.getAbsolutePath();
    }

    /**
     * Pretvara entitet u DTO koji vraćamo ka frontendu.
     */
    private ConfigResponse toResponse(ImageConfig cfg) {
        return ConfigResponse.builder()
                .id(cfg.getId())
                .scaleDown(cfg.getScaleDown() * 100.0) // ponovo konvertuj iz 0.05 u 5%
                .logoPosition(cfg.getPosition())
                .logoPath(cfg.getLogoPath())
                .build();
    }
}
