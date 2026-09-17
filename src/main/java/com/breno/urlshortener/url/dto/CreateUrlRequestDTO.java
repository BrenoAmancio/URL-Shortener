package com.breno.urlshortener.url.dto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Nullable;
import java.util.Date;

public record CreateUrlRequestDTO(
        @NotBlank(message = "URL required") String url,
        @Future(message = "The expiration date must be later than the current date") @DateTimeFormat Date expiresAt,
        HttpServletRequest request
) {
}
