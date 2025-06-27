package com.example.redirectservice.client;

import com.example.redirectservice.model.ShortenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "url-shortner-service"
)
public interface UrlShortnerClient {

    @GetMapping("/shorten/{shortCode}")
    ShortenResponse getOriginalUrl(@PathVariable String shortCode);
}
