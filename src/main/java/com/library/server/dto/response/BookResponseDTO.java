package com.library.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDTO {
    private Integer id;
    private String isbn;
    private String title;
    private String authorName;
    private String categoryName;
    private String publisherName;
    private Integer publishedYear;
    private Integer totalQty;
    private Integer availableQty;
    private String imageUrl;
    private String location;
    private String description;
}