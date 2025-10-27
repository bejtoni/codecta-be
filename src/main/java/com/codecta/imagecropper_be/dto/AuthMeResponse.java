package com.codecta.imagecropper_be.dto;

import lombok.*;

import java.util.UUID;

/** Jednostavan, stabilan response za FE. */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AuthMeResponse {
    private UUID userId;
    private String email;
}
