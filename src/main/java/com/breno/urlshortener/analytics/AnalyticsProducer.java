package com.breno.urlshortener.analytics;

import com.breno.urlshortener.analytics.DTO.UrlAnalyticsEventDTO;
import com.breno.urlshortener.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsProducer {
    private final RabbitTemplate rabbitTemplate;

    public AnalyticsProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(UrlAnalyticsEventDTO event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                "url-analytics." + event.eventType().name().toLowerCase(),
                event
        );
    }
}
