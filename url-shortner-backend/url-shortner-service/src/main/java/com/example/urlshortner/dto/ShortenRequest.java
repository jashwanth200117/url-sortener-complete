package com.example.urlshortner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class ShortenRequest {

    @NotNull(message = "originalUrl is required")
    @NotBlank(message = "originalUrl must not be empty")
    @URL(message = "Please enter a valid URL (e.g., https://example.com)")
    private String originalUrl;
}

