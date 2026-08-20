package com.breno.urlshortener.url.dto;

import java.time.Instant;
import java.util.UUID;

public record CreateUrlResponse(
        UUID id,
        String shortCode,
        String urlShort,
        Instant createdAt,
        Instant expiresAt
) {
}
