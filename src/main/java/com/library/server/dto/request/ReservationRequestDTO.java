package com.library.server.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationRequestDTO {
    
    // ✅ SECURITY: userId không bắt buộc - lấy từ JWT token
    // FE không cần gửi userId trong request body
    private Integer userId;
    
    @NotNull(message = "Book ID không được null")
    @Positive(message = "Book ID phải > 0")
    private Integer bookId;
    
    // Reservation date do BE set khi duyệt, không lấy từ client
    private LocalDateTime reservationDate;
    
    @NotBlank(message = "Status không được trống")
    @Pattern(
        regexp = "^(PENDING|APPROVED|CANCELLED|COMPLETED)$",
        message = "Status phải là PENDING, APPROVED, CANCELLED, hoặc COMPLETED"
    )
    private String status;
}
