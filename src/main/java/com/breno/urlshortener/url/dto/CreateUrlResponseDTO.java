package com.breno.urlshortener.url.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public record CreateUrlResponseDTO(
        @NotBlank UUID id,
        @NotBlank String shortCode,
        @NotBlank String urlShort,
        @NotBlank Instant createdAt,
        Instant expiresAt
) {
}
