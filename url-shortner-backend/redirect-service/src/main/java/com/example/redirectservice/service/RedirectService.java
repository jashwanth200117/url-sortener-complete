package com.example.redirectservice.service;

import com.example.redirectservice.client.UrlShortnerClient;
import com.example.redirectservice.exception.UrlNotFoundException;
import com.example.redirectservice.model.ShortenResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RedirectService {

    @Autowired
    private UrlShortnerClient urlShortenerClient;

    /**
     * Retrieve the original URL for the given short code
     */

    @Retry(name = "shortenerRetry", fallbackMethod = "handleFailure")
    @CircuitBreaker(name = "shortenerCircuitBreaker", fallbackMethod = "handleFailure")
    public String getOriginalUrl(String shortCode) {
        ShortenResponse response = urlShortenerClient.getOriginalUrl(shortCode);

        if (response == null || !response.isSuccess()) {
            throw new UrlNotFoundException("Short URL not found: " + shortCode);
        }

        return response.getShortUrl();
    }

    public String handleFailure(String shortCode, Throwable throwable) {
        System.out.println("🔁 Fallback activated for shortCode: " + shortCode + " | Reason: " + throwable.getMessage());
        throw new UrlNotFoundException("Short URL not found or service unavailable: " + shortCode);
    }
}
