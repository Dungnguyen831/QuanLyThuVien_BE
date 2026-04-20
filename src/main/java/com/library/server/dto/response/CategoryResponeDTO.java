package com.library.server.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
@Data
public class CategoryResponeDTO {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
