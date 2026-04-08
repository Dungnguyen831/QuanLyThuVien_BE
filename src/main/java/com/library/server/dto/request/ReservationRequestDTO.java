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
    
    @NotNull(message = "User ID không được null")
    @Positive(message = "User ID phải > 0")
    private Integer userId;
    
    @NotNull(message = "Book ID không được null")
    @Positive(message = "Book ID phải > 0")
    private Integer bookId;
    
    @NotNull(message = "Reservation date không được null")
    @FutureOrPresent(message = "Reservation date phải là hiện tại hoặc tương lai")
    private LocalDateTime reservationDate;
    
    @NotBlank(message = "Status không được trống")
    @Pattern(
        regexp = "^(PENDING|APPROVED|CANCELLED|COMPLETED)$",
        message = "Status phải là PENDING, APPROVED, CANCELLED, hoặc COMPLETED"
    )
    @Size(max = 50, message = "Status không được vượt quá 50 ký tự")
    private String status;
}

