package com.example.gateway.controller;

//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseCookie;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import reactor.core.publisher.Mono;
//
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    // ✅ GET /auth/validate — checks if token is valid
//    @GetMapping("/validate")
//    public ResponseEntity<String> validate(ServerHttpRequest request) {
//        String username = request.getHeaders().getFirst("X-User-Name");
//
//        if (username != null && !username.isEmpty()) {
//            return ResponseEntity.ok(username);
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//    }
//
//    // ✅ POST /auth/logout — clears cookie
//    @PostMapping("/logout")
//    public Mono<ResponseEntity<Void>> logout(@CookieValue(name = "jwt", required = false) String token,
//                                             ServerHttpResponse response) {
//        if (token != null) {
//            // 🔐 Create an expired cookie to remove JWT
//            ResponseCookie expiredCookie = ResponseCookie.from("jwt", "")
//                    .httpOnly(true)
//                    .secure(true) // Set to true in production
//                    .path("/")
//                    .maxAge(0) // Expire immediately
//                    .sameSite("None")
//                    .build();
//
//            response.addCookie(expiredCookie);
//        }
//
//        return Mono.just(ResponseEntity.ok().build());
//    }
//}

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
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