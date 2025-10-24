package com.codecta.imagecropper_be.service;

import com.codecta.imagecropper_be.dto.CropRectangleDto;
import com.codecta.imagecropper_be.entity.ImageConfig;
import com.codecta.imagecropper_be.exception.NotFoundException;
import com.codecta.imagecropper_be.repository.ImageConfigRepository;
import com.codecta.imagecropper_be.utility.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageConfigRepository configRepo;

    /**
     * PREVIEW endpoint logika: read → crop → 5% scale → PNG bytes.
     * (Već si imao ovu metodu — ostavi je ako postoji, prikazana je radi kompletnosti.)
     */
    public byte[] preview(MultipartFile image, CropRectangleDto rect) throws IOException {
        validatePng(image);
        BufferedImage src = ImageUtils.readPng(image.getInputStream());
        BufferedImage cropped = ImageUtils.cropRectangle(src, rect);
        BufferedImage scaled = ImageUtils.scalePercent(cropped, 0.05); // fiksnih 5%
        return ImageUtils.toPngBytes(scaled);
    }

    /**
     * GENERATE endpoint logika: read → crop (full quality) → opcionalni logo overlay → PNG bytes.
     * - Ako je prosleđen configId, nalazimo njegov zapis u bazi i primjenjujemo logo.
     * - scaleDown je već u bazi kao DECIMAL faktor (0.01–0.25), tj. 1–25% od crop W/H.
     */
    public byte[] generate(MultipartFile image, CropRectangleDto rect, String configId) throws IOException {
        validatePng(image);

        // 1) učitaj originalni PNG
        BufferedImage src = ImageUtils.readPng(image.getInputStream());

        // 2) cropaj na pravougaonik (full quality)
        BufferedImage cropped = ImageUtils.cropRectangle(src, rect);

        // 3) ako je configId poslan — učitaj config i nacrtaj logo
        if (configId != null && !configId.isBlank()) {
            ImageConfig cfg = configRepo.findById(UUID.fromString(configId))
                    .orElseThrow(() -> new NotFoundException("Config not found: " + configId));

            if (cfg.getLogoPath() != null) {
                // učitaj logo sa diska (PNG s alpha kanalom)
                File logoFile = new File(cfg.getLogoPath());
                if (logoFile.exists()) {
                    try (var in = Files.newInputStream(logoFile.toPath())) {
                        BufferedImage logo = ImageIO.read(in);
                        // scaleDown u bazi je već fractions (npr. 0.12 = 12% širine/visine croppanog outputa)
                        double pct = cfg.getScaleDown();
                        cropped = ImageUtils.overlayLogo(cropped, logo, cfg.getPosition(), pct, pct);
                    }
                }
            }
        }

        // 4) spakuj u PNG byte[] za HTTP response
        return ImageUtils.toPngBytes(cropped);
    }

    // --- helpers ---

    private void validatePng(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }
        if (!"image/png".equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException("Only image/png is supported");
        }
    }
}
