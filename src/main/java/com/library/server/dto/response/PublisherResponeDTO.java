package com.library.server.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PublisherResponeDTO {
    private Integer id;
    private String name;
    private String address;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
