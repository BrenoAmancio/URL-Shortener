package com.breno.urlshortener.analytics.DTO;

public record AnalyticsInfoDTO(
        UserAgentInfo userInfoDTO,
        String country,
        String state,
        String referrer
) {
}
