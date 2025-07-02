package com.example.auth.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/authh")
public class AuthController {

    @GetMapping("/validate")
    public ResponseEntity<String> validate(ServerHttpRequest request) {
        System.out.println("We have entered the validateeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");


        String username = request.getHeaders().getFirst("X-User-Name");

        System.out.println("This is usernameeeee "+ username);
        if (username != null && !username.isEmpty()) {
            return ResponseEntity.ok(username);
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(ServerHttpResponse response) {
        System.out.println("We have entered the validateeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        // 🔐 Clear the cookie by setting it with maxAge = 0
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        response.addCookie(cookie);

        return Mono.just(ResponseEntity.ok().build());
    }
}