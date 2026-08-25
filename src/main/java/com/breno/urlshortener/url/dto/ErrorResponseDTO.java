package com.breno.urlshortener.url.dto;

import java.time.Instant;

public record ErrorResponseDTO(
        String code,
        Instant timestamp,
        String message
) {
}
