package com.library.server.dto.request;

import lombok.Data;

@Data
public class BookRequestDTO {
    private String title;
    private String isbn;
    private Integer publishedYear;
    private Integer totalQty;
    private Integer availableQty;
    private String imageUrl;
    private Integer categoryId;
    private Integer authorId;
    private Integer publisherId;
}