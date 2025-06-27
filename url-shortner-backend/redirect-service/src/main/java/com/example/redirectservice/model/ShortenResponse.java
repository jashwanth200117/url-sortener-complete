package com.example.redirectservice.model;

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