package com.codecta.imagecropper_be.controller;

import com.codecta.imagecropper_be.dto.AuthMeResponse;
import com.codecta.imagecropper_be.entity.User;
import com.codecta.imagecropper_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * AuthController:
 * - /auth/google - POST endpoint koji prima Bearer Google ID token i upsert-uje usera
 * - /api/auth/me - GET endpoint koji vraća userId i email za authenticated usera
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * POST /auth/google
     * Prima Google ID token kao Bearer token i upsert-uje usera po email-u.
     * Frontend šalje: Authorization: Bearer <GOOGLE_ID_TOKEN>
     */
    @PostMapping("/auth/google")
    public ResponseEntity<Map<String, Object>> loginWithGoogle(@AuthenticationPrincipal Jwt jwt) {
        // Jwt je već validiran od strane Spring Security resource server-a
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        String picture = jwt.getClaimAsString("picture");

        User user = userService.upsertFromJwt(jwt);

        return ResponseEntity.ok(Map.of(
                "userId", user.getId(),
                "email", email != null ? email : "",
                "name", name != null ? name : "",
                "picture", picture != null ? picture : ""
        ));
    }

    /**
     * GET /api/auth/me
     * Vrati informacije o trenutno authenticated useru.
     */
    @GetMapping("/api/auth/me")
    public ResponseEntity<AuthMeResponse> me(@AuthenticationPrincipal Jwt jwt) {
        // @AuthenticationPrincipal Jwt je već validiran (issuer = Google)
        User user = userService.upsertFromJwt(jwt);

        return ResponseEntity.ok(AuthMeResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .build());
    }
}
