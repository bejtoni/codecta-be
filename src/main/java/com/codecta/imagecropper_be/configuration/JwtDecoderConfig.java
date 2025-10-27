package com.codecta.imagecropper_be.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;

import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * Konfiguracija JWT dekodera sa validacijom audience-a.
 * Provjerava da JWT token sadrži pravi GOOGLE_CLIENT_ID u aud claim-u.
 */
@Configuration
public class JwtDecoderConfig {

    @Value("${google.client-id}")
    private String clientId;

    @Bean
    public JwtDecoder jwtDecoder() {
        String issuer = "https://accounts.google.com";
        NimbusJwtDecoder decoder = JwtDecoders.fromIssuerLocation(issuer);

        // Default validatori (issuer, expiration, etc.)
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);

        // Audience validator - provjeri da aud claim sadrži naš GOOGLE_CLIENT_ID
        OAuth2TokenValidator<Jwt> withAudience = jwt -> {
            var audience = jwt.getAudience();
            boolean isValid = audience != null && audience.contains(clientId);
            
            if (isValid) {
                return OAuth2TokenValidatorResult.success();
            } else {
                return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Token audience does not match expected client ID", "")
                );
            }
        };

        // Kombinuj sve validatore
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience));
        
        return decoder;
    }
}

