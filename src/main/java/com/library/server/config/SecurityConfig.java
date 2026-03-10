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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Cú pháp mới cho CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Thay authorizeRequests() bằng authorizeHttpRequests()
                .authorizeHttpRequests(auth -> auth
                        // 3. Thay antMatchers() bằng requestMatchers()
                        .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login","/api/v1/users","/api/v1/users/{id}" ).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}