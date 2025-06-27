package com.example.auth.service;

import com.example.auth.dto.AuthRequest;
import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Register a new user
    public Mono<AuthResponse> register(RegisterRequest request) {
        System.out.println("Received register request: " + request);

        return userRepository.findByUsername(request.getUsername())
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        System.out.println("User already exists for username: " + request.getUsername());
                        return Mono.error(new RuntimeException("Username already exists!"));
                    } else {
                        User newUser = User.builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role("ROLE_USER")
                                .build();

                        System.out.println("Saving new user: " + newUser);

                        return userRepository.save(newUser)
                                .doOnNext(savedUser -> System.out.println("User saved to DB: " + savedUser))
                                .map(savedUser -> {
                                    String token = jwtService.generateToken(savedUser.getUsername(), savedUser.getRole());
                                    System.out.println("Generated JWT Token: " + token);
                                    return new AuthResponse(token);
                                });
                    }
                })
                .doOnError(error -> {
                    System.err.println("Error during registration: " + error.getMessage());
                    error.printStackTrace();
                });
    }



    // Authenticate a user and return JWT
    public Mono<AuthResponse> authenticate(AuthRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .switchIfEmpty(Mono.error(new RuntimeException("User not found!")))
                .flatMap(user -> {
                    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return Mono.just(new AuthResponse(
                                jwtService.generateToken(user.getUsername(), user.getRole())
                        ));
                    } else {
                        return Mono.error(new RuntimeException("Invalid credentials!"));
                    }
                });
    }

    public String getUsernameFromToken(String token) {
        return jwtService.extractUsername(token);
    }

}
