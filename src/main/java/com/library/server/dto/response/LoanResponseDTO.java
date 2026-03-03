package com.library.server.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanResponseDTO {
    private String id;              // Mã phiếu (VD: MP001)
    private String userName;        // Tên Nguyễn Văn A
    private String userAvatarColor; // Màu ngẫu nhiên
    private String bookName;        // Tên sách
    private String borrowDate;
    private String dueDate;
    private String returnDate;
    private String status;
}