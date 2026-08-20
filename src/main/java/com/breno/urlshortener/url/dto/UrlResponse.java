package com.breno.urlshortener.url.dto;

import java.time.Instant;
import java.util.UUID;

public record UrlResponse(
        UUID id,
        String shortCode,
        String originalUrl,
        String shortUrl,
        Instant createdAt,
        Instant expiresAt
) {}
