package com.example.analytics.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "click_events")
public class ClickEvent {
    @Id
    private String id;
    private String shortCode;
    private String originalUrl;
    private String userAgent;
    private String ipAddress;
    private Long timestamp; // UNIX timestamp in ms
}
