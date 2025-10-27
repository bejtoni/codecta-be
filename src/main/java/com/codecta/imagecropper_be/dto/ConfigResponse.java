package com.codecta.imagecropper_be.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigResponse {
    private UUID id;
    private double scaleDown; // normalizovana vrijednost (0.01-0.25)
    private String logoPosition; // string reprezentacija pozicije
    private Boolean hasLogo; // da li korisnik ima logo
}
