package com.codecta.imagecropper_be.controller;

import com.codecta.imagecropper_be.dto.ConfigResponse;
import com.codecta.imagecropper_be.entity.User;
import com.codecta.imagecropper_be.enums.LogoPosition;
import com.codecta.imagecropper_be.service.ConfigService;
import com.codecta.imagecropper_be.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;
    private final UserService userService;

    /**
     * GET /api/config/me
     * Vrati korisnikov config ili 404
     */
    @Operation(summary = "Get current user's configuration")
    @GetMapping("/me")
    public ResponseEntity<ConfigResponse> getMyConfig(Authentication authentication) {
        // Izvuci JWT i upsertuj korisnika ako ne postoji
        Jwt jwt = (Jwt) authentication.getPrincipal();
        User user = userService.upsertFromJwt(jwt);
        
        // Dohvati config za korisnika
        ConfigResponse response = configService.getConfigByUserId(user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/config (CREATE ONLY, multipart)
     * Kreiraj novu konfiguraciju - svi parametri su obavezni
     */
    @Operation(summary = "Create new configuration (all fields required)")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ConfigResponse> createConfig(
            Authentication authentication,
            @RequestParam("scaleDownPercent") String scaleDownPercentStr,
            @RequestParam("logoPosition") String position,
            @RequestParam("logoImage") MultipartFile logo
    ) throws IOException {
        
        // Parse scaleDownPercent from string to double
        double scaleDownPercent = Double.parseDouble(scaleDownPercentStr);
        
        // Izvuci JWT i upsertuj korisnika ako ne postoji
        Jwt jwt = (Jwt) authentication.getPrincipal();
        User user = userService.upsertFromJwt(jwt);
        
        // Kreiraj config - servis baca grešku ako već postoji
        ConfigResponse response = configService.createConfig(user.getId(), scaleDownPercent, position, logo);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/config/me
     * Parcijalno ažuriranje - sva polja su opciona (multipart podržava i JSON podatke i fajlove)
     */
    @Operation(summary = "Update configuration (partial update - all fields optional)")
    @PutMapping(value = "/me", consumes = {"multipart/form-data"})
    public ResponseEntity<ConfigResponse> updateConfig(
            Authentication authentication,
            @RequestParam(name = "scaleDownPercent", required = false) String scaleDownPercentStr,
            @RequestParam(name = "logoPosition", required = false) String positionStr,
            @RequestParam(name = "logoImage", required = false) MultipartFile logo
    ) throws IOException {
        
        // Izvuci JWT i upsertuj korisnika ako ne postoji
        Jwt jwt = (Jwt) authentication.getPrincipal();
        User user = userService.upsertFromJwt(jwt);
        
        // Parse opciona polja
        Double scaleDown = scaleDownPercentStr != null ? Double.parseDouble(scaleDownPercentStr) : null;
        LogoPosition position = positionStr != null ? LogoPosition.valueOf(positionStr.toUpperCase()) : null;
        
        // Ažuriraj config - samo poslana polja se mijenjaju
        ConfigResponse response = configService.updateConfig(user.getId(), scaleDown, position, logo);
        return ResponseEntity.ok(response);
    }
}
