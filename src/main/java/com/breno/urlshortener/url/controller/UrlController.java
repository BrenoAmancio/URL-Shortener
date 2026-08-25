package com.breno.urlshortener.url.controller;

import com.breno.urlshortener.url.dto.CreateUrlRequestDTO;
import com.breno.urlshortener.url.dto.CreateUrlResponseDTO;
import com.breno.urlshortener.url.dto.UrlResponseDTO;
import com.breno.urlshortener.url.service.UrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @PostMapping("/api/create")
    public UrlResponseDTO createShortUrl(@RequestBody CreateUrlRequestDTO dto) {
        CreateUrlResponseDTO urlShorted = this.urlService.createShortUrl(dto);

        return new UrlResponseDTO(
                urlShorted.id(),
                urlShorted.shortCode(),
                dto.url(),
                urlShorted.urlShort(),
                urlShorted.createdAt(),
                urlShorted.expiresAt()
        );
    }
}
