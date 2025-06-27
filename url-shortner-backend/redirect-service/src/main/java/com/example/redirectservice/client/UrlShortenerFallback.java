package com.example.redirectservice.client;

import com.example.redirectservice.model.ShortenResponse;
import org.springframework.stereotype.Component;

@Component
public class UrlShortenerFallback implements UrlShortnerClient {
    @Override
    public ShortenResponse getOriginalUrl(String shortCode) {
        System.out.println("Fallback activated for shortCode: " + shortCode);
        return new ShortenResponse(false, "Service unavailable right now", null);
    }
}
