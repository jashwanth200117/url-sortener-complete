package com.example.redirectservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClickEvent {
    private String shortCode;
    private String originalUrl;
    private String userAgent;
    private String ipAddress;
    private long timestamp;
}
