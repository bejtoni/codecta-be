package com.codecta.imagecropper_be.utility;

import com.codecta.imagecropper_be.dto.CropRectangleDto;
import com.codecta.imagecropper_be.enums.LogoPosition;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public final class ImageUtils {
    private ImageUtils() {}

    public static BufferedImage readPng(InputStream in) throws IOException {
        BufferedImage img = ImageIO.read(in);
        if (img == null) throw new IOException("Invalid PNG");
        return img;
    }

    public static BufferedImage cropRectangle(BufferedImage src, CropRectangleDto r) {
        // clamp ako je potrebno (da ne izađemo van slike)
        int x = Math.max(0, r.getX());
        int y = Math.max(0, r.getY());
        int w = Math.min(r.getWidth(),  src.getWidth()  - x);
        int h = Math.min(r.getHeight(), src.getHeight() - y);
        if (w <= 0 || h <= 0) throw new IllegalArgumentException("Crop rect out of bounds");

        // TYPE_INT_ARGB da zadržimo transparentnost
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        try {
            g.drawImage(src, 0, 0, w, h, x, y, x + w, y + h, null);
        } finally {
            g.dispose();
        }
        return out;
    }

    public static BufferedImage scalePercent(BufferedImage src, double factor) throws IOException {
        int w = Math.max(1, (int)Math.round(src.getWidth()  * factor));
        int h = Math.max(1, (int)Math.round(src.getHeight() * factor));
        return Thumbnails.of(src).size(w, h).asBufferedImage();
    }

    public static byte[] toPngBytes(BufferedImage img) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }

    public static BufferedImage overlayLogo(
            BufferedImage base,
            BufferedImage logo,
            LogoPosition position,
            double logoPercentOfCropW,
            double logoPercentOfCropH
    ) throws IOException {

        // Izračun ciljne veličine loga prema dimenzijama croppanog outputa
        int targetW = Math.max(1, (int) Math.round(base.getWidth() * logoPercentOfCropW));
        int targetH = Math.max(1, (int) Math.round(base.getHeight() * logoPercentOfCropH));

        // Skaliraj logo na ciljnu veličinu (zadržava alpha kanal)
        BufferedImage scaledLogo = Thumbnails.of(logo)
                .size(targetW, targetH)
                .asBufferedImage();

        // Odredi X/Y prema izabranoj poziciji
        int x = switch (position) {
            case TOP_LEFT, BOTTOM_LEFT -> 0;
            case TOP_RIGHT, BOTTOM_RIGHT -> base.getWidth() - scaledLogo.getWidth();
            case CENTER -> (base.getWidth() - scaledLogo.getWidth()) / 2;
        };

        int y = switch (position) {
            case TOP_LEFT, TOP_RIGHT -> 0;
            case BOTTOM_LEFT, BOTTOM_RIGHT -> base.getHeight() - scaledLogo.getHeight();
            case CENTER -> (base.getHeight() - scaledLogo.getHeight()) / 2;
        };

        // Crtamo u novi ARGB bitmap kako bismo sačuvali transparentnost
        BufferedImage out = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(base, 0, 0, null);
            g.drawImage(scaledLogo, x, y, null);
        } finally {
            g.dispose();
        }

        return out;
    }
}
