package com.example.auth.config;

//import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.security.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@EnableReactiveMethodSecurity
@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    public SecurityConfig(UserRepository userRepository,
                          JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.userRepository = userRepository;
        this.jwtAuthorizationFilter  = jwtAuthorizationFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        ReactiveAuthenticationManager authManager = authentication -> Mono.just(authentication);
        AuthenticationWebFilter jwtAuthWebFilter = new AuthenticationWebFilter(authManager);
        jwtAuthWebFilter.setServerAuthenticationConverter(jwtAuthorizationFilter);
        jwtAuthWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/hello", "/user/**")); // secure APIs

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**").permitAll() // login/register
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return (String username) -> userRepository.findByUsername(username)
                .map(user -> User
                        .withUsername(user.getUsername())
                        .password(user.getPassword()) // Make sure it's already encoded!
                        .roles(user.getRole().replace("ROLE_", "")) // Remove "ROLE_" prefix if needed
                        .build());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
