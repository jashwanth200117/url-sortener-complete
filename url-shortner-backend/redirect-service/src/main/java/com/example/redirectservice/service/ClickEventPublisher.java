package com.example.redirectservice.service;

import com.example.redirectservice.model.ClickEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClickEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC = "click-events";

    public ClickEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void publishClickEvent(ClickEvent clickEvent) {
        try {
            String message = objectMapper.writeValueAsString(clickEvent);
            System.out.println("The string message is:" + message);
            kafkaTemplate.send(TOPIC, clickEvent.getShortCode(), message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
