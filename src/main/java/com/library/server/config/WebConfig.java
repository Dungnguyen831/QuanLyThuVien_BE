package com.library.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Đường dẫn này PHẢI giống hệt uploadDir trong UploadController
        String uploadDir = "C:\\xampp\\htdocs\\QuanLyThuVien_FE\\assets\\img";
        Path uploadPath = Paths.get(uploadDir);
        String absolutePath = uploadPath.toFile().getAbsolutePath();

        // Ánh xạ URL /api/v1/images/** vào thư mục vật lý
        registry.addResourceHandler("/api/v1/images/**")
                .addResourceLocations("file:/" + absolutePath + "/");
    }
}