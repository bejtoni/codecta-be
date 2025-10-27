package com.codecta.imagecropper_be.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security konfiguracija:
 * - dozvoli Swagger i OpenAPI rute bez tokena
 * - /auth/** zahtijeva Bearer JWT (Google ID token)
 * - /api/** zahtijeva Bearer JWT
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF off za čisti REST (nema browser session formi)
                .csrf(csrf -> csrf.disable())
                
                // CORS support
                .cors(Customizer.withDefaults())

                // Autorizacija ruta
                .authorizeHttpRequests(auth -> auth
                        // Swagger / OpenAPI
                        .requestMatchers(
                                "/swagger/**",
                                "/api-docs/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // /auth/google prima Bearer ID token
                        .requestMatchers("/auth/**").authenticated()
                        
                        // Sve ostalo ispod /api/** traži JWT
                        .requestMatchers("/api/**").authenticated()

                        // Bilo šta drugo (npr. health) možeš po potrebi otvoriti
                        .anyRequest().permitAll()
                )

                // Resource server sa JWT podrškom (Google OIDC)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
