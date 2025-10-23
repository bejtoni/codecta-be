package com.codecta.imagecropper_be.utility;

import com.codecta.imagecropper_be.dto.CropRectangleDto;
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
}
