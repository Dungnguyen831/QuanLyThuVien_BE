package com.library.server.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthorResponseDTO {
    private Integer id;
    private String name;
    private String biography;
    private String country;
    private Integer bookcount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
