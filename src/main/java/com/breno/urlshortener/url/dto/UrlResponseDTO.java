package com.breno.urlshortener.url.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public record UrlResponseDTO(
        @NotBlank UUID id,
        @NotBlank String shortCode,
        @NotBlank String originalUrl,
        @NotBlank String shortUrl,
        @NotBlank Instant createdAt,
        Instant expiresAt
) {}
