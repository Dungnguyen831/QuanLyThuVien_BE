package com.library.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistResponseDTO {
    private Integer userId;
    private Integer bookId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

