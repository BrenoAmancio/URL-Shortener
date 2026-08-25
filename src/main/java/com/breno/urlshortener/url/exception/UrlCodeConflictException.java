package com.breno.urlshortener.url.exception;

public class UrlCodeConflictException extends RuntimeException {
    public UrlCodeConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
