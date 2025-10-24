package com.codecta.imagecropper_be.controller;

import com.codecta.imagecropper_be.dto.GenerateRequestDto;
import com.codecta.imagecropper_be.dto.PreviewRequestDto;
import com.codecta.imagecropper_be.service.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Operation(summary = "Preview: rectangle crop → 5% scaled PNG (binary)")
    @PostMapping(value = "/preview", consumes = {"multipart/form-data"})
    public ResponseEntity<byte[]> preview(
            @RequestPart("file") MultipartFile file,
            @RequestPart("meta") String metaJson
    ) throws IOException {
        PreviewRequestDto meta = mapper.readValue(metaJson, PreviewRequestDto.class);
        byte[] png = imageService.preview(file, meta.getRect());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }

    @Operation(summary = "Generate: rectangle crop (full quality) + optional logo overlay (configId)")
    @PostMapping(value = "/generate", consumes = {"multipart/form-data"})
    public ResponseEntity<byte[]> generate(
            @RequestPart("file") MultipartFile file,
            @RequestPart("meta") String metaJson
    ) throws IOException {
        // meta: {"rect": {...}, "configId": "UUID-..."}   // configId je opciono
        GenerateRequestDto meta = mapper.readValue(metaJson, GenerateRequestDto.class);
        byte[] png = imageService.generate(file, meta.getRect(), meta.getConfigId());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }
}
