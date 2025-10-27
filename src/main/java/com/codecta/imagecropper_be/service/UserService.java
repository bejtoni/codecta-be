package com.codecta.imagecropper_be.service;

import com.codecta.imagecropper_be.entity.User;
import com.codecta.imagecropper_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Upsert korisnika iz Google OIDC JWT-a:
     * - provider je "GOOGLE"
     * - providerUserId = jwt.getSubject() (sub)
     * - email = jwt.getClaim("email")
     */
    @Transactional
    public User upsertFromJwt(Jwt jwt) {
        final String provider = "GOOGLE";
        final String providerUserId = jwt.getSubject();
        final String email = jwt.getClaimAsString("email");

        // Minimalna provjera — sub mora postojati
        if (providerUserId == null || providerUserId.isBlank()) {
            throw new IllegalStateException("Missing 'sub' claim in JWT");
        }

        // Ako korisnik postoji po providerUserId → update email-a ako se promijenio
        return userRepository.findByProviderUserId(providerUserId)
                .map(existing -> {
                    if (email != null && !email.equalsIgnoreCase(existing.getEmail())) {
                        existing.setEmail(email);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    // Kreiraj novog
                    return userRepository.save(User.builder()
                            .provider(provider.toUpperCase(Locale.ROOT))
                            .providerUserId(providerUserId)
                            .email(email) // može biti null ako provider ne vraća (Google vraća)
                            .build());
                });
    }
}
