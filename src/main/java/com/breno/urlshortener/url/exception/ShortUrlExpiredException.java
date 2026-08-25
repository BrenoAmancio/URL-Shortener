package com.breno.urlshortener.url.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ShortUrlExpiredException extends RuntimeException {
    public ShortUrlExpiredException(String shortCode) {
        super("Short URL expired: " + shortCode);
    }
}
