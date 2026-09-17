package com.breno.urlshortener.url.service;

import com.breno.urlshortener.url.dto.ShortUrlCacheDTO;
import com.breno.urlshortener.url.entity.ShortUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class CacheService {
    private final RedisTemplate<String, ShortUrlCacheDTO> redisTemplate;
    private final Logger logger = LoggerFactory.getLogger(CacheService.class);

    public CacheService(RedisTemplate<String, ShortUrlCacheDTO> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void tryCacheShortUrl (String key, ShortUrl shortUrl) {
        try {
            ShortUrlCacheDTO dto = new ShortUrlCacheDTO(shortUrl.getId(), shortUrl.getOriginalUrl(), shortUrl.getExpiresAt());
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

    public ShortUrlCacheDTO tryGetCache(String key) {
        try {
            logger.debug("Trying to get key from Redis: '{}'", key);

            ShortUrlCacheDTO cached = redisTemplate.opsForValue().get(key);

            logger.debug("Redis result for key {}: '{}'", key, cached);

            return cached;
        } catch (Exception e) {
            logger.warn("Failed to get key {} from Redis", key, e);
            return null;
        }
    }

    public void tryDeleteCache(String key) {
        try {
            logger.debug("Deleting key from Redis: '{}'", key);

            Boolean deleted = redisTemplate.delete(key);

            logger.debug("Key {} deleted from Redis: '{}'", key, deleted);
        } catch (Exception e) {
            logger.warn("Failed to delete key {} from Redis", key, e);
        }
    }
}
