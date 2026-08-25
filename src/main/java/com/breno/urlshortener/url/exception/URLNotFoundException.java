package com.breno.urlshortener.url.exception;

public class URLNotFoundException extends RuntimeException {
  public URLNotFoundException(String shortCode) {
    super("Short URL not found: " + shortCode);
  }
}
