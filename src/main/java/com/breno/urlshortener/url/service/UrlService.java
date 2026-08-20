package com.breno.urlshortener.url.service;

import com.breno.urlshortener.url.dto.CreateUrlRequest;
import com.breno.urlshortener.url.dto.CreateUrlResponse;
import com.breno.urlshortener.url.entity.ShortUrl;
import com.breno.urlshortener.url.respository.UrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final CodeService codeService;

    @Value("${server.domain}")
    private String domain;

    public UrlService(UrlRepository userRepository, CodeService codeService) {
        this.urlRepository = userRepository;
        this.codeService = codeService;
    }

    public CreateUrlResponse createShortUrl(CreateUrlRequest dto) {
        ShortUrl shortUrl = new ShortUrl(
            codeService.generate(),
            dto.url(),
            dto.expiresAt()
        );
        ShortUrl saved = urlRepository.save(shortUrl);

        return new CreateUrlResponse(
                saved.getId(),
                saved.getCode(),
                domain + saved.getCode(),
                saved.getCreatedAt(),
                saved.getExpiresAt()
        );
    }

    public String getOriginalUrl(String shortCode) {
        ShortUrl shortUrl = urlRepository.findByCode(shortCode).orElse(null);

        if (shortUrl == null) return null;
        if (shortUrl.getExpiresAt() != null && shortUrl.getExpiresAt().isBefore(Instant.now())) {
            return null;
        }

        return shortUrl.getOriginalUrl();
    }
}
