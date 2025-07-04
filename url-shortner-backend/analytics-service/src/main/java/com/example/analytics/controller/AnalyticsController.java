package com.example.analytics.controller;

import com.example.analytics.dto.ClickEventResponse;
import com.example.analytics.model.ClickEvent;
import com.example.analytics.repository.ClickEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final ClickEventRepository clickEventRepository;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{shortCode}")
    public ResponseEntity<Map<String, Object>> getAnalytics(
            @PathVariable String shortCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ClickEvent> pageResult = clickEventRepository.findByShortCode(shortCode, pageable);

        if (pageResult.isEmpty()) {
            log.warn("No analytics found for short code: {}", shortCode);
            return ResponseEntity.notFound().build();
        }

        List<ClickEventResponse> responses = pageResult.getContent().stream()
                .map(event -> {
                    ClickEventResponse dto = new ClickEventResponse();
                    dto.setShortCode(event.getShortCode());
                    dto.setOriginalUrl(event.getOriginalUrl());
                    dto.setIpAddress(event.getIpAddress());
                    dto.setUserAgent(event.getUserAgent());
                    dto.setAccessedAt(Instant.ofEpochMilli(event.getTimestamp())
                            .atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ISO_DATE_TIME));
                    return dto;
                }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("clicks", responses);
        response.put("currentPage", pageResult.getNumber());
        response.put("totalPages", pageResult.getTotalPages());
        response.put("totalItems", pageResult.getTotalElements());

        return ResponseEntity.ok(response);
    }


    /**
     * ✅ GET Click Count Summary
     */
    @GetMapping("/{shortCode}/summary")
    public ResponseEntity<Map<String, Object>> getClickSummary(@PathVariable String shortCode) {
        long clickCount = clickEventRepository.countByShortCode(shortCode);

        Map<String, Object> summary = new HashMap<>();
        summary.put("shortCode", shortCode);
        summary.put("clickCount", clickCount);

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        System.out.println("Authorization Header received: " + authHeader);

        return ResponseEntity.ok("Check logs");
    }

    @GetMapping("/debug")
    public String debugRoles(Authentication auth) {
        System.out.println("User: " + auth.getName());
        auth.getAuthorities().forEach(a -> System.out.println("Authority: " + a.getAuthority()));
        return "Check logs";
    }
}
