package com.breno.urlshortener.url.service;

import com.breno.urlshortener.url.dto.CreateUrlRequestDTO;
import com.breno.urlshortener.url.dto.CreateUrlResponseDTO;
import com.breno.urlshortener.url.dto.ShortUrlCacheDTO;
import com.breno.urlshortener.url.entity.ShortUrl;
import com.breno.urlshortener.url.exception.ShortUrlExpiredException;
import com.breno.urlshortener.url.exception.URLNotFoundException;
import com.breno.urlshortener.url.exception.UrlCodeConflictException;
import com.breno.urlshortener.url.exception.UrlShorteningException;
import com.breno.urlshortener.url.respository.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final CodeService codeService;
    private final RedisTemplate<String, ShortUrlCacheDTO> redisTemplate;
    private final Logger logger = LoggerFactory.getLogger(UrlService.class);
    private static final String CACHE_PREFIX = "shorturl:";

    @Value("${server.domain}")
    private String domain;

    public UrlService(UrlRepository userRepository, CodeService codeService, RedisTemplate<String, ShortUrlCacheDTO> redisTemplate) {
        this.urlRepository = userRepository;
        this.codeService = codeService;
        this.redisTemplate = redisTemplate;
    }

    public CreateUrlResponseDTO createShortUrl(CreateUrlRequestDTO dto) {
        Instant expiresAt = (dto.expiresAt() != null) ? dto.expiresAt().toInstant() : null;
        String code = codeService.generate();

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

        return new CreateUrlResponseDTO(
                saved.getId(),
                saved.getCode(),
                domain + saved.getCode(),
                saved.getCreatedAt(),
                saved.getExpiresAt()
        );
    }

    public String getOriginalUrl(String shortCode) {
        String key = CACHE_PREFIX + shortCode;

        ShortUrlCacheDTO cached = tryGetCache(key);
        if(cached != null) {
            if (cached.expiresAt() != null && cached.expiresAt().isBefore(Instant.now())) {
                tryDeleteCache(key);
                return null;
            }

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

        logger.debug("Resolved short code {} -> {}", shortCode, shortUrl.getOriginalUrl());
        tryCacheShortUrl(key, shortUrl);
        return shortUrl.getOriginalUrl();
    }

    private void tryCacheShortUrl (String key, ShortUrl shortUrl) {
        try {
            ShortUrlCacheDTO dto = new ShortUrlCacheDTO(shortUrl.getOriginalUrl(), shortUrl.getExpiresAt());
            Duration ttl = (shortUrl.getExpiresAt() != null) ? Duration.between(Instant.now(), shortUrl.getExpiresAt()) : Duration.ofHours(24);

            if (ttl.isNegative() || ttl.isZero()) {
                logger.debug("Invalid TTL to code {}, it wont be cached", shortUrl.getCode());
                return;
            }

            redisTemplate.opsForValue().set(key, dto, ttl);
            logger.debug("Cached code '{}' with ttl={}", shortUrl.getCode(), ttl);
        } catch (Exception e) {
            logger.warn("Failed to cache short URL: code={}", shortUrl.getCode(), e);
        }
    }

    private ShortUrlCacheDTO tryGetCache(String key) {
        try {
//            return redisTemplate.opsForValue().get(key);
            logger.debug("Trying to get key from Redis: '{}'", key);

            ShortUrlCacheDTO cached = redisTemplate.opsForValue().get(key);

            logger.debug("Redis result for key {}: '{}'", key, cached);

            return cached;
        } catch (Exception e) {
            logger.warn("Failed to get key {} from Redis", key, e);
            return null;
        }
    }

    private void tryDeleteCache(String key) {
        try {
            logger.debug("Deleting key from Redis: '{}'", key);

            Boolean deleted = redisTemplate.delete(key);

            logger.debug("Key {} deleted from Redis: '{}'", key, deleted);
        } catch (Exception e) {
            logger.warn("Failed to delete key {} from Redis", key, e);
        }
    }
}
