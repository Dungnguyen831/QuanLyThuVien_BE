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
    public class ReviewResponseDTO {
        private Integer id;
        private Integer userId;
        private String fullName;  // ✅ NEW: User's full name
        private Integer bookId;
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

