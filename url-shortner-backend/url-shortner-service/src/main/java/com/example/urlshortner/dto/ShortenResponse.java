package com.example.urlshortner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortenResponse {
    private boolean success;
    private String message;
    private String shortUrl;
}
