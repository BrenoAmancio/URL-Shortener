package com.breno.urlshortener.url.exception;

public class UrlShorteningException extends RuntimeException {
    public UrlShorteningException(String message, Throwable cause) {
        super(message, cause);
    }
}
