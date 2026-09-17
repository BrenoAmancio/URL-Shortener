package com.breno.urlshortener.url.service;

import com.breno.urlshortener.analytics.AnalyticsProducer;
import com.breno.urlshortener.analytics.DTO.UrlAnalyticsEventDTO;
import com.breno.urlshortener.analytics.enums.UrlAnalyticsEventENUM;
import com.breno.urlshortener.url.dto.CreateUrlRequestDTO;
import com.breno.urlshortener.url.dto.CreateUrlResponseDTO;
import com.breno.urlshortener.url.dto.ShortUrlCacheDTO;
import com.breno.urlshortener.url.entity.ShortUrl;
import com.breno.urlshortener.url.exception.ShortUrlExpiredException;
import com.breno.urlshortener.url.exception.URLNotFoundException;
import com.breno.urlshortener.url.exception.UrlCodeConflictException;
import com.breno.urlshortener.url.exception.UrlShorteningException;
import com.breno.urlshortener.url.respository.UrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final CodeService codeService;
    private final Logger logger = LoggerFactory.getLogger(UrlService.class);
    private static final String CACHE_PREFIX = "shorturl:";
    private final CacheService cacheService;
    private final AnalyticsProducer producer;

    @Value("${server.domain}")
    private String domain;

    public UrlService(
            UrlRepository userRepository,
            CodeService codeService,
            CacheService cacheService,
            AnalyticsProducer producer
    ) {
        this.urlRepository = userRepository;
        this.codeService = codeService;
        this.cacheService = cacheService;
        this.producer = producer;
    }

    public CreateUrlResponseDTO createShortUrl(CreateUrlRequestDTO dto) {
        Instant expiresAt = (dto.expiresAt() != null) ? dto.expiresAt().toInstant() : null;
        String code = codeService.generate();
        HttpServletRequest request = dto.request();

        ShortUrl shortUrl = new ShortUrl(
            code,
            dto.url(),
            expiresAt
        );

        ShortUrl saved;
        try {
            saved = urlRepository.saveAndFlush(shortUrl);
        } catch (UrlCodeConflictException e) {
            logger.warn("Code collision when saving shortened URL: code={}", code);
            throw new UrlCodeConflictException("Generated code already exists", e);
        } catch (DataAccessException e) {
            logger.error("Database error while saving short URL: code={}", code, e);
            throw new UrlShorteningException("Error saving the shortened URL to the database", e);
        }

        logger.info("Short URL created successfully: id={}, code={}", saved.getId(), saved.getCode());

        String key = CACHE_PREFIX + saved.getCode();
        cacheService.tryCacheShortUrl(key, saved);

        producer.publish(new UrlAnalyticsEventDTO(
                saved.getId(),
                UrlAnalyticsEventENUM.CREATED,
                Instant.now(),
                getIpAddress(request),
                request.getHeader("User-Agent"),
                request.getHeader("Referer")
        ));

        return new CreateUrlResponseDTO(
                saved.getId(),
                saved.getCode(),
                domain + saved.getCode(),
                saved.getCreatedAt(),
                saved.getExpiresAt()
        );
    }

    public String getOriginalUrl(String shortCode, HttpServletRequest request) {
        String key = CACHE_PREFIX + shortCode;

        ShortUrlCacheDTO cached = cacheService.tryGetCache(key);
        if(cached != null) {
            if (cached.expiresAt() != null && cached.expiresAt().isBefore(Instant.now())) {
                cacheService.tryDeleteCache(key);
                return null;
            }

            this.producer.publish(new UrlAnalyticsEventDTO(
                    cached.id(),
                    UrlAnalyticsEventENUM.ACCESSED,
                    Instant.now(),
                    getIpAddress(request),
                    request.getHeader("User-Agent"),
                    request.getHeader("Referer")
            ));

            return cached.originalUrl();
        }

        ShortUrl shortUrl = urlRepository.findByCode(shortCode).orElseThrow(() -> {
            logger.warn("Short code not found: {}", shortCode);
            return new URLNotFoundException(shortCode);
        });

        if (shortUrl.getExpiresAt() != null && shortUrl.getExpiresAt().isBefore(Instant.now())) {
            logger.warn("Short code expired: {} (expired_at={})", shortCode, shortUrl.getExpiresAt());
            throw new ShortUrlExpiredException(shortCode);
        }

        this.producer.publish(new UrlAnalyticsEventDTO(
                shortUrl.getId(),
                UrlAnalyticsEventENUM.ACCESSED,
                Instant.now(),
                getIpAddress(request),
                request.getHeader("User-Agent"),
                request.getHeader("Referer")
        ));

        logger.debug("Resolved short code {} -> {}", shortCode, shortUrl.getOriginalUrl());
        cacheService.tryCacheShortUrl(key, shortUrl);
        return shortUrl.getOriginalUrl();
    }

    private String getIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        return (forwardedFor != null && !forwardedFor.isBlank())
                ? forwardedFor.split(",")[0].trim()
                : request.getRemoteAddr();
    }
}
