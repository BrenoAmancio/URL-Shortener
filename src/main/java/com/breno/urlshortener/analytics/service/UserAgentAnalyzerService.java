package com.breno.urlshortener.analytics.service;

import com.breno.urlshortener.analytics.DTO.GeoLocation;
import com.breno.urlshortener.analytics.DTO.UserAgentInfo;
import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Service;

@Service
public class UserAgentAnalyzerService {
    private final UserAgentAnalyzer uaa = UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(1000).build();

    public UserAgentInfo getUserAgentInfo(String userAgent) {
        if (userAgent == null) return unknownUserInfo();

        UserAgent ua = uaa.parse(userAgent);

        String operatingSystem = ua.getValue("OperatingSystemName");
        String deviceType =  ua.getValue("DeviceClass");
        String browser  = ua.getValue("AgentName");

        return new UserAgentInfo(operatingSystem, deviceType, browser);
    }

    private UserAgentInfo unknownUserInfo() {
        return new UserAgentInfo(null, null, null);
    }

    public boolean isValidUserInfo(UserAgentInfo userAgentInfo) {
        return userAgentInfo.operatingSystem() != null && userAgentInfo.deviceType() != null && userAgentInfo.browser() != null;
    }
}
