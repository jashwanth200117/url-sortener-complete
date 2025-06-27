package com.example.analytics.dto;

import lombok.Data;

@Data
public class ClickEventResponse {
    private String shortCode;
    private String originalUrl;
    private String ipAddress;
    private String userAgent;
    private String accessedAt; // formatted timestamp (e.g. ISO 8601)
}
