package com.example.gateway.config;

import com.example.gateway.filter.JwtAuthenticationFilter;
//import io.netty.handler.codec.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        String authHeader = http.headers().getClass(HttpHeaders.AUTHORIZATION);
//        System.out.println("JWT Filter - Authorization header: " + authHeader);

        return http
                .cors(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/shorten").permitAll()
                        .pathMatchers("/auth/**").permitAll()  // ✅ allow login/register without token
                        .pathMatchers("/redirect/**").permitAll()
                        .anyExchange().authenticated()         // 🔒 protect all other paths
                )
                .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
