
package com.example.urlshortner.controller;

import com.example.urlshortner.dto.ShortenRequest;
import com.example.urlshortner.dto.ShortenResponse;
import com.example.urlshortner.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/shorten")
public class UrlShortenerController {

    @Autowired
    private UrlShortenerService service;

    @PostMapping
    public ResponseEntity<ShortenResponse> createShortUrl(@Valid @RequestBody ShortenRequest request ,
                                                          @RequestHeader(value = "X-User-Name", required = false) String username
    ) {
        String user = (username != null) ? username : "guest";

        String originalUrl = request.getOriginalUrl();
        String shortenedUrl = service.shortenUrl(originalUrl , user);

        System.out.println("user " + user);
        System.out.println("shortenedUrl " + shortenedUrl);

        ShortenResponse response = new ShortenResponse(
                true,
                "Short URL created successfully",
                shortenedUrl
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> getOriginalUrl(@PathVariable String shortCode) {
        if ("fail".equals(shortCode)) {
            throw new RuntimeException("Forced failure for testing retry/circuit breaker");
        }
        String originalUrl = service.getOriginalUrl(shortCode);

        if (originalUrl == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Short URL not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ShortenResponse response = new ShortenResponse(
                true,
                "Original URL retrieved successfully",
                originalUrl
        );

        return ResponseEntity.ok(response);
    }
}
