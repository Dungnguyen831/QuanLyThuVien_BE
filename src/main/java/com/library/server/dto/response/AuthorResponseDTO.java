package com.library.server.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthorResponseDTO {
    private Integer id;
    private String name;
    private String biography;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
