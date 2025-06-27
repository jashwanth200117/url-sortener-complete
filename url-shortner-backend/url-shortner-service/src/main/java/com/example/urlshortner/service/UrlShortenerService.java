
package com.example.urlshortner.service;

import com.example.urlshortner.entity.UrlMapping;
import com.example.urlshortner.exception.UrlNotFoundException;
import com.example.urlshortner.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UrlShortenerService {

    @Autowired
    private UrlMappingRepository repository;



    public String shortenUrl(String originalUrl, String createdBy) {
        String shortCode = generateShortCode();
        UrlMapping mapping = UrlMapping.builder()
                .shortCode(shortCode)
                .originalUrl(originalUrl)
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
        repository.save(mapping);
        return shortCode;
    }

    public String getOriginalUrl(String shortCode) {
        Optional<UrlMapping> optional = repository.findByShortCode(shortCode);
        if (optional.isEmpty()) {
            throw new UrlNotFoundException("Short URL not found");
        }
        return optional.get().getOriginalUrl();
    }

    private String generateShortCode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}
