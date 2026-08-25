package com.breno.urlshortener.url.common;

import com.breno.urlshortener.url.dto.ErrorResponseDTO;
import com.breno.urlshortener.url.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(URLNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(URLNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(
                "SHORT_URL_NOT_FOUND",
                Instant.now(),
                e.getMessage())
        );
    }

    @ExceptionHandler(ShortUrlExpiredException.class)
    public ResponseEntity<ErrorResponseDTO> handleExpired(ShortUrlExpiredException e) {
        return ResponseEntity.status(HttpStatus.GONE).body(new ErrorResponseDTO(
                "SHORT_URL_EXPIRED",
                Instant.now(),
                e.getMessage())
        );
    }

    @ExceptionHandler(CodeGenerationException.class)
    public ResponseEntity<ErrorResponseDTO> handleCodeGeneration(CodeGenerationException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(
                "CODE_GENERATION_FAILED",
                Instant.now(),
                "Unable to generate short URL")
        );
    }

    @ExceptionHandler(UrlCodeConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleCodeConflict(UrlCodeConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDTO(
                "CODE_CONFLICT",
                Instant.now(),
                e.getMessage()
        ));
    }

    @ExceptionHandler(UrlShorteningException.class)
    public ResponseEntity<ErrorResponseDTO> handleinternal (UrlShorteningException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(
                "INTERNAL_ERROR",
                Instant.now(),
                "Unexpected error"
        ));
    }
}
