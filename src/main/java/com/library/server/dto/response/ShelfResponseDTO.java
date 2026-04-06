package com.library.server.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ShelfResponseDTO  {
    private Integer id;
    private String name;
    private Integer floor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
