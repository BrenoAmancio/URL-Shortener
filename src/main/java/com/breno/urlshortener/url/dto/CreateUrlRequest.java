package com.breno.urlshortener.url.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;

import java.time.Instant;
import java.util.Date;

public record CreateUrlRequest(
        @NotBlank String url,
        Instant expiresAt
) {
}
