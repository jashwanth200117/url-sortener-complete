package com.example.gateway.filter;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import jakarta.annotation.PostConstruct;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Value("${keycloak.public-key}")
    private String keycloakPublicKey;

    private RSAPublicKey publicKey;
    @PostConstruct
    public void init() {
        try {
            String cleaned = keycloakPublicKey
                    .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", ""); // remove newlines and spaces

            byte[] decoded = Base64.getDecoder().decode(cleaned);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

            System.out.println("✅ Public key loaded successfully!");
        } catch (Exception e) {
            System.err.println("❌ Error loading public key:");
            e.printStackTrace(); // Add this to get full error in console
            throw new RuntimeException("Failed to load public key", e);
        }
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/shorten") || path.startsWith("/redirect") || path.startsWith("/users/")) {
            return chain.filter(exchange);
        }

        String token = null;

        // 🔍 Try getting token from cookie
        if (exchange.getRequest().getCookies().getFirst("jwt") != null) {
            token = exchange.getRequest().getCookies().getFirst("jwt").getValue();
        }

        System.out.println("First token valueeeeeeeeee------------->"+token);

        // If still null, try from Authorization header (optional)
        if ((token == null || token.isEmpty()) &&
                exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }
        System.out.println("Second token valueeeeeeeeee------------->"+token);

        if (token == null || token.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }


        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // 🔐 Verify Signature
            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            if (!signedJWT.verify(verifier)) {
                System.out.println("Failed the verification------------->"+token);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            String username = claims.getStringClaim("preferred_username");

            // ✅ Extract roles from realm_access.roles
            List<String> roles = ((Map<String, List<String>>) claims.getClaim("realm_access")).get("roles");

            System.out.println("✅ Extracted roles from token:");
            for (String role : roles) {
                System.out.println("   - " + role);
            }

            // 🔧 Convert roles to GrantedAuthority
            List<GrantedAuthority> authorities = roles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase())
                    .map(r -> {
                        System.out.println("🛡️ Mapping to GrantedAuthority: " + r);
                        return new SimpleGrantedAuthority(r);
                    })
                    .collect(Collectors.toList());


            ServerWebExchange mutated = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-User-Name", username)
                            .header("Authorization", "Bearer " + token)
                            .build())
                    .build();

            // Set authentication object with roles
            Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);

            System.out.println("✅ Authentication object created with " + authorities.size() + " authorities");
            // Inject it into Spring Security context
            return chain.filter(mutated)
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(
                            Mono.just(new SecurityContextImpl(auth))
                    ));

        } catch (Exception e) {
            e.printStackTrace();
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
