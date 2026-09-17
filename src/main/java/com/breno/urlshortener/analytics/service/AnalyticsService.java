package com.breno.urlshortener.analytics.service;

import com.breno.urlshortener.analytics.DTO.AnalyticsInfoDTO;
import com.breno.urlshortener.analytics.DTO.GeoLocation;
import com.breno.urlshortener.analytics.DTO.UrlAnalyticsEventDTO;
import com.breno.urlshortener.analytics.DTO.UserAgentInfo;
import com.breno.urlshortener.analytics.entity.Metric;
import com.breno.urlshortener.analytics.repository.AnalyticsRepository;
import com.breno.urlshortener.url.entity.ShortUrl;
import com.breno.urlshortener.url.respository.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {
//    private final RedisTemplate<String, Integer> redisTemplate;
    private final String ANALYTICS_PREFIX = "analytics:";
    private final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);
    private final GeoLocationService geoLocationService;
    private final UserAgentAnalyzerService userAgentAnalyzerService;
    private final AnalyticsRepository analyticsRepository;
    private final UrlRepository urlRepository;

    public AnalyticsService(
            GeoLocationService geoLocationService,
            UserAgentAnalyzerService userAgentAnalyzerService,
            AnalyticsRepository analyticsRepository,
            UrlRepository urlRepository
    ) {
        this.geoLocationService = geoLocationService;
        this.userAgentAnalyzerService = userAgentAnalyzerService;
        this.analyticsRepository = analyticsRepository;
        this.urlRepository = urlRepository;
    }

//    public AnalyticsService(RedisTemplate<String, Integer> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }

    public void created(UrlAnalyticsEventDTO event) {
        logger.info("ANALYTICS_PREFIX + event.eventType()");
    }

    public void accessed(UrlAnalyticsEventDTO event) {
        GeoLocation geoLocation = geoLocationService.getGeoLocation(event.ipAddress());
        UserAgentInfo userAgentInfo = userAgentAnalyzerService.getUserAgentInfo(event.userAgent());
        String referer = event.referer();

        if (geoLocationService.isValidGeoLocation(geoLocation) && userAgentAnalyzerService.isValidUserInfo(userAgentInfo)) return;
        ShortUrl url = urlRepository.getReferenceById(event.urlId());
        Metric metric = new Metric(
                url,
                event.timestamp(),
                userAgentInfo.operatingSystem(),
                userAgentInfo.deviceType(),
                userAgentInfo.browser(),
                geoLocation.state(),
                geoLocation.country(),
                referer
        );

        analyticsRepository.save(metric);
    }
}
