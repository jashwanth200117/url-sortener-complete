package com.example.auth.controller;

import com.example.auth.dto.AuthRequest;
import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Register endpoint
    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody RegisterRequest request,
                                                 ServerHttpResponse response) {
        return authService.register(request)
                .map(authResponse -> {
                    ResponseCookie cookie = ResponseCookie.from("jwt", authResponse.getToken())
                            .httpOnly(true)
                            .secure(false) // Set to true in production with HTTPS
                            .path("/")
                            .maxAge(Duration.ofDays(1))
                            .sameSite("Lax")
                            .build();

                    response.addCookie(cookie);
                    return ResponseEntity.ok("Registration successful");
                })
                .onErrorResume(error ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(error.getMessage()))
                );
    }

    // Login endpoint
    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody AuthRequest request,
                                              ServerHttpResponse response) {
        return authService.authenticate(request)
                .map(authResponse -> {
                    ResponseCookie cookie = ResponseCookie.from("jwt", authResponse.getToken())
                            .httpOnly(true)
                            .secure(false)
                            .path("/")
                            .maxAge(Duration.ofDays(1))
                            .sameSite("Lax")
                            .build();

                    response.addCookie(cookie);
                    return ResponseEntity.ok("Login successful");
                })
                .onErrorResume(error ->
                        Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(error.getMessage()))
                );
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(ServerHttpResponse response) {
        // Create expired cookie
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true) // only send over HTTPS
                .path("/")
                .maxAge(0) // expire immediately
                .sameSite("None") // if cross-origin
                .build();

        response.addCookie(cookie);

        return Mono.just(ResponseEntity.ok().build());
    }

    @GetMapping("/validate")
    public Mono<ResponseEntity<String>> validate(@CookieValue(name = "jwt", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token"));
        }

        try {
            String username = authService.getUsernameFromToken(token);
            return Mono.just(ResponseEntity.ok(username));
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token"));
        }
    }


}
