package com.library.server.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // Tên của scheme bảo mật

        return new OpenAPI()
                .info(new Info().title("Library API").version("1.0"))
                // 1. Thêm yêu cầu bảo mật chung cho toàn bộ API (ngoại trừ những cái được permitAll)
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // 2. Cấu hình Components để định nghĩa "bearerAuth" là gì
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
