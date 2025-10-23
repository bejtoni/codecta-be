package com.codecta.imagecropper_be.service;

import com.codecta.imagecropper_be.dto.CropRectangleDto;
import com.codecta.imagecropper_be.utility.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageService {

    public byte[] preview(MultipartFile image, CropRectangleDto rect) throws IOException {
        if (image == null || image.isEmpty())
            throw new IllegalArgumentException("Image file is required");
        if (!"image/png".equalsIgnoreCase(image.getContentType()))
            throw new IllegalArgumentException("Only image/png is supported");

        BufferedImage src = ImageUtils.readPng(image.getInputStream());
        BufferedImage cropped = ImageUtils.cropRectangle(src, rect);
        BufferedImage scaled = ImageUtils.scalePercent(cropped, 0.05); // fiksnih 5%
        return ImageUtils.toPngBytes(scaled);
    }
}
