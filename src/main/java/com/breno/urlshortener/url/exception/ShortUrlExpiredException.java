package com.breno.urlshortener.url.exception;

public class ShortUrlExpiredException extends RuntimeException {
    public ShortUrlExpiredException(String shortCode) {
        super("Short URL expired: " + shortCode);
    }
}
