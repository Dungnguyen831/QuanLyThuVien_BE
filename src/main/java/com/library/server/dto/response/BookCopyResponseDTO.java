package com.library.server.dto.response;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookCopyResponseDTO {
    private Integer id;
    private Integer shelfId;
    private String shelfName; // Để hiển thị tên kệ thay vì ID
    private String barcode;
    private String conditionStatus;
    private String availabilityStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}