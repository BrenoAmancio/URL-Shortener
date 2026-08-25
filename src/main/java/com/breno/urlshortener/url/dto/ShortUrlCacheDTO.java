package com.breno.urlshortener.url.dto;

import java.time.Instant;

public record ShortUrlCacheDTO(
        String originalUrl,
        Instant expiresAt
) {}
