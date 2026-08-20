package com.breno.urlshortener.url.controller;

import com.breno.urlshortener.url.dto.CreateUrlRequest;
import com.breno.urlshortener.url.dto.CreateUrlResponse;
import com.breno.urlshortener.url.dto.UrlResponse;
import com.breno.urlshortener.url.service.UrlService;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;

@RestController
public class UrlController {
    @Autowired
    private UrlService urlService;
    private final Logger logger = LoggerFactory.getLogger(UrlController.class);

//    public UrlController(UrlService urlService) {
//        this.urlService = urlService;
//    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);

        if (originalUrl == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @PostMapping("/api/create")
    public UrlResponse createShortUrl(@RequestBody CreateUrlRequest dto) {
        logger.debug("DTO Request: " + dto);
        CreateUrlResponse urlShorted = this.urlService.createShortUrl(dto);

        return new UrlResponse(
                urlShorted.id(),
                urlShorted.shortCode(),
                dto.url(),
                urlShorted.urlShort(),
                Instant.now(),
                Instant.now());
    }
}
