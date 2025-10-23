package com.codecta.imagecropper_be.controller;

import com.codecta.imagecropper_be.dto.*;
import com.codecta.imagecropper_be.enums.LogoPosition;
import com.codecta.imagecropper_be.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService service;

    /**
     * Endpoint za kreiranje novog configa.
     * Koristi multipart formu jer može sadržavati i fajl (logo).
     */
    @Operation(summary = "Create new logo configuration")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ConfigResponse> create(
            @RequestPart("scaleDown") double scaleDown,
            @RequestPart("logoPosition") LogoPosition logoPosition,
            @RequestPart(name = "logoImage", required = false) MultipartFile logoImage
    ) throws IOException {

        // Pretvori logoPosition iz stringa u enum
        CreateConfigRequest req = new CreateConfigRequest(scaleDown, logoPosition, logoImage);

        // Pozovi servis koji vrši validaciju, snimanje i čuvanje u bazu
        ConfigResponse response = service.create(req);

        // Vrati HTTP 200 + kreirani config kao JSON
        return ResponseEntity.ok(response);
    }


    /**
     * Endpoint za djelimično ažuriranje postojećeg configa.
     * Sve vrijednosti su opcionalne – možeš promijeniti samo jednu stvar (npr. novi logo).
     */
    @Operation(summary = "Update existing configuration")
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ConfigResponse> update(
            @PathVariable UUID id,
            @RequestPart(required = false) Double scaleDown,
            @RequestPart(required = false) LogoPosition logoPosition,
            @RequestPart(name = "logoImage", required = false) MultipartFile logoImage
    ) throws IOException {
        // Napravi DTO sa onim što je korisnik poslao (ostalo ostaje null)
        UpdateConfigRequest req = new UpdateConfigRequest(scaleDown, logoPosition, logoImage);

        // Pozovi servis da izvrši update i vrati novi rezultat
        ConfigResponse updated = service.update(id, req);

        return ResponseEntity.ok(updated);
    }


    /**
     * Dohvati jedan config po ID-u.
     * Koristi se za preview u frontendu ili test u Swaggeru.
     */
    @Operation(summary = "Get config by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ConfigResponse> get(@PathVariable UUID id) {
        ConfigResponse response = service.get(id);
        return ResponseEntity.ok(response);
    }
}
