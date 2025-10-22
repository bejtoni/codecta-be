package com.codecta.imagecropper_be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
    @PostMapping("/preview")
    public ResponseEntity<Integer> previewImage(){
        return null;
    };

    @PostMapping("/generate")
    public ResponseEntity<Integer> generateImage(){
        return null;
    };

}
