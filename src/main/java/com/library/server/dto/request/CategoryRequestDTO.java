package com.library.server.dto.request;

import lombok.Data;

@Data
public class CategoryRequestDTO {
    private String name;
    private String description;
    private Integer bookcount;
}

