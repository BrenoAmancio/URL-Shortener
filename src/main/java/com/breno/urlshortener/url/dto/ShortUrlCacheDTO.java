package com.breno.urlshortener.url.dto;

import java.time.Instant;
import java.util.UUID;

public record ShortUrlCacheDTO(
        UUID id,
        String originalUrl,
        Instant expiresAt
) {}
