package com.library.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //  SWAGGER  http://localhost:8080/swagger-ui/index.html
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth

                        // Swagger endpoints
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // API public
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/users/**",
                                "/api/v1/books/**",
                                "/api/v1/book-copies/**",
                                "/api/v1/authors/**",
                                "/api/v1/shelves/**",
                                "/api/v1/publishers/**",
                                "/api/v1/loans/**",
                                "/api/v1/categories"
                        ).permitAll()

                        .anyRequest().authenticated()
                );
        return http.build();
    }
}