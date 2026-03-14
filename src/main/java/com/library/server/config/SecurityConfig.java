package com.library.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Cho phép tất cả các request OPTIONS (tránh lỗi Preflight 403)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/auth", "/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/books", "/api/v1/books/**").permitAll()
                        .requestMatchers("/api/v1/users", "/api/v1/users/**").permitAll()
                        .requestMatchers("/api/v1/book-copies", "/api/v1/book-copies/**").permitAll()
                        .requestMatchers("/api/v1/authors", "/api/v1/authors/**").permitAll()
                        .requestMatchers("/api/v1/shelves", "/api/v1/shelves/**").permitAll()
                        .requestMatchers("/api/v1/publishers", "/api/v1/publishers/**").permitAll()
                        .requestMatchers("/api/v1/loans", "/api/v1/loans/**").permitAll()
                        .requestMatchers("/api/v1/categories", "/api/v1/categories/**").permitAll()
                        .requestMatchers("/api/v1/reservations", "/api/v1/reservations/**").permitAll()
                        .requestMatchers("/api/v1/reviews", "/api/v1/reviews/**").permitAll()
                        .requestMatchers("/api/v1/wishlists", "/api/v1/wishlists/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}