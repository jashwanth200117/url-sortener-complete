package com.example.redirectservice.controller;

import com.example.redirectservice.model.ClickEvent;
import com.example.redirectservice.service.RedirectService;
import com.example.redirectservice.service.ClickEventPublisher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redirect")
@RequiredArgsConstructor
public class RedirectController {

    private static final Logger logger = LoggerFactory.getLogger(RedirectController.class);

    private final RedirectService redirectService;
    private final ClickEventPublisher clickEventPublisher;

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {
        logger.info("Redirect request received for shortCode: {}", shortCode);

        String originalUrl = redirectService.getOriginalUrl(shortCode); // Now synchronous

        // 🔥 Create and publish ClickEvent
        ClickEvent clickEvent = new ClickEvent(
                shortCode,
                originalUrl,
                request.getHeader("User-Agent"),
                request.getRemoteAddr(),
                System.currentTimeMillis()
        );
        clickEventPublisher.publishClickEvent(clickEvent);

        logger.info("Redirecting to: {}", originalUrl);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", originalUrl)
                .build();
    }
}
