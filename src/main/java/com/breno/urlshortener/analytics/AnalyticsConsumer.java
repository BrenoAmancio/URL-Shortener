package com.breno.urlshortener.analytics;

import com.breno.urlshortener.analytics.DTO.UrlAnalyticsEventDTO;
import com.breno.urlshortener.analytics.enums.UrlAnalyticsEventENUM;
import com.breno.urlshortener.analytics.service.AnalyticsService;
import com.breno.urlshortener.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsConsumer {
    private final AnalyticsService analyticsService;

    public AnalyticsConsumer(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void consume(UrlAnalyticsEventDTO event) {
        switch (event.eventType()){
            case UrlAnalyticsEventENUM.CREATED -> analyticsService.created(event);
            case UrlAnalyticsEventENUM.ACCESSED -> analyticsService.accessed(event);
        }
    }
}
