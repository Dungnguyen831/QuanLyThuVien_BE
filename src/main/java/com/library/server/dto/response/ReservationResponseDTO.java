package com.library.server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationResponseDTO {
    @JsonProperty("reservation_id")
    private Integer id;

    @JsonProperty("book_id")
    private Integer bookId;

    @JsonProperty("book_copy_barcode")
    private String bookCopyBarcode;

    private Integer userId;

    private String userName;     // Tên sinh viên
    private String userEmail;    // Email sinh viên
    private String bookName;     // Tên sách

    @JsonProperty("reservation_date")
    private LocalDateTime reservationDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
