package com.codecta.imagecropper_be.service;

import com.codecta.imagecropper_be.dto.CropRectangleDto;
import com.codecta.imagecropper_be.entity.ImageConfig;
import com.codecta.imagecropper_be.repository.ImageConfigRepository;
import com.codecta.imagecropper_be.utility.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageConfigRepository configRepo;

    /**
     * PREVIEW endpoint logika: read → crop → 5% scale → PNG bytes.
     */
    public byte[] preview(MultipartFile image, CropRectangleDto rect) throws IOException {
        validatePng(image);
        BufferedImage src = ImageUtils.readPng(image.getInputStream());
        validateRect(src, rect);
        BufferedImage cropped = ImageUtils.cropRectangle(src, rect);
        BufferedImage scaled = ImageUtils.scalePercent(cropped, 0.05); // fiksnih 5%
        return ImageUtils.toPngBytes(scaled);
    }

    /**
     * GENERATE endpoint logika: read → crop (full quality) → opcionalni logo overlay → PNG bytes.
     * - Koristi userId za lookup configa (ne configId iz requesta)
     * - scaleDown je već u bazi kao DECIMAL faktor (0.01–0.25), tj. 1–25% od crop W/H.
     */
    public byte[] generate(MultipartFile image, CropRectangleDto rect, UUID userId) throws IOException {
        validatePng(image);

        // 1) učitaj originalni PNG
        BufferedImage src = ImageUtils.readPng(image.getInputStream());
        
        // 2) validiraj rect u odnosu na originalnu sliku
        validateRect(src, rect);

        // 3) cropaj na pravougaonik (full quality)
        BufferedImage cropped = ImageUtils.cropRectangle(src, rect);

        // 4) učitaj user config i nacrtaj logo ako postoji
        ImageConfig config = configRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User configuration not found"));

        if (config.getLogoBlob() != null && config.getLogoBlob().length > 0) {
            // učitaj logo iz blob-a (PNG s alpha kanalom)
            BufferedImage logo = ImageIO.read(new ByteArrayInputStream(config.getLogoBlob()));
            if (logo != null) {
                // scaleDown u bazi je već fractions (npr. 0.12 = 12% širine/visine croppanog outputa)
                double scaleDown = config.getScaleDownPercent();
                cropped = ImageUtils.overlayLogo(cropped, logo, config.getPosition(), scaleDown, scaleDown);
            }
        }

        // 5) spakuj u PNG byte[] za HTTP response
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

    /**
     * Validira da li je rect unutar granica originalne slike
     */
    private void validateRect(BufferedImage src, CropRectangleDto rect) {
        if (rect.getX() < 0 || rect.getY() < 0) {
            throw new IllegalArgumentException("Rectangle coordinates must be >= 0");
        }
        if (rect.getWidth() <= 0 || rect.getHeight() <= 0) {
            throw new IllegalArgumentException("Rectangle dimensions must be > 0");
        }
        if (rect.getX() + rect.getWidth() > src.getWidth()) {
            throw new IllegalArgumentException("Rectangle extends beyond image width");
        }
        if (rect.getY() + rect.getHeight() > src.getHeight()) {
            throw new IllegalArgumentException("Rectangle extends beyond image height");
        }
    }
}
