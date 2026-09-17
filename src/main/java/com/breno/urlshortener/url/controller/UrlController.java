package com.breno.urlshortener.url.controller;

import com.breno.urlshortener.analytics.DTO.GeoLocation;
import com.breno.urlshortener.analytics.DTO.UserAgentInfo;
import com.breno.urlshortener.analytics.service.GeoLocationService;
import com.breno.urlshortener.analytics.service.UserAgentAnalyzerService;
import com.breno.urlshortener.url.dto.CreateUrlRequestDTO;
import com.breno.urlshortener.url.dto.CreateUrlResponseDTO;
import com.breno.urlshortener.url.dto.UrlResponseDTO;
import com.breno.urlshortener.url.service.UrlService;
import com.maxmind.geoip2.model.CityResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {
    private final UrlService urlService;
    private final Logger logger = LoggerFactory.getLogger(UrlController.class);
    private final UserAgentAnalyzerService userAgentAnalyzerService;

    public UrlController(UrlService urlService,  UserAgentAnalyzerService userAgentAnalyzerService) {
        this.urlService = urlService;
        this.userAgentAnalyzerService = userAgentAnalyzerService;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            HttpServletRequest request
    ) {

        String originalUrl = urlService.getOriginalUrl(shortCode, request);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

//    @GetMapping("/test")
//    public UserAgentInfo test(HttpServletRequest request) {
//        return userAgentAnalyzerService.getUserAgentInfo(request);
//    }

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
