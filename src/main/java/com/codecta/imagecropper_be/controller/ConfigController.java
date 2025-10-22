package com.codecta.imagecropper_be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {
    @PostMapping
    public ResponseEntity<String> updateConfiguration(){
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateConfigurationById(@PathVariable("id") String id){
        return null;
    }
}
