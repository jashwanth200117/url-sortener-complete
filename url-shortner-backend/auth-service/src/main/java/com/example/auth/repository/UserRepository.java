package com.example.auth.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.example.auth.entity.User;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByUsername(String username);
}
