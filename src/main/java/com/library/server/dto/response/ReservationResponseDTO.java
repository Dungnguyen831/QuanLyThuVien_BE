package com.library.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationResponseDTO {
    private Integer id;
    private Integer userId;
    private Integer bookId;

    private String userName;     // Tên sinh viên
    private String userEmail;    // Email sinh viên
    private String bookName;     // Tên sách

    private LocalDate reservationDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

