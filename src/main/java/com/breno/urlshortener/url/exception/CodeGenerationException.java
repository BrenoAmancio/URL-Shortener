package com.breno.urlshortener.url.exception;

public class CodeGenerationException extends RuntimeException {
    public CodeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
