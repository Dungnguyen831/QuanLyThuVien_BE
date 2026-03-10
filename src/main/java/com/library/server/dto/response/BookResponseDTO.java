package com.library.server.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {
    private Integer id;
    private String title;
    private String isbn;
    private Integer publishedYear;
    private Integer totalQty;
    private Integer availableQty;
    private String imageUrl;

    // Chỉ trả về ID thay vì cả Object phức tạp
    private Integer categoryId;
    private Integer authorId;
    private Integer publisherId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}