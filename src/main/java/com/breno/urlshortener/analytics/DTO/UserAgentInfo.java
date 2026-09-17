package com.breno.urlshortener.analytics.DTO;

public record UserAgentInfo(
        String operatingSystem,
        String deviceType,
        String browser
) {
}
