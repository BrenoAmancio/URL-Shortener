package com.breno.urlshortener.analytics.DTO;

import com.breno.urlshortener.analytics.enums.UrlAnalyticsEventENUM;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public record UrlAnalyticsEventDTO(
        UUID urlId,
        UrlAnalyticsEventENUM eventType,
        Instant timestamp,
        String ipAddress,
        String userAgent,
        String referer
) {
}
