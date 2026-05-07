package com.library.server.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanResponseDTO {
    private String id;
    private Integer loanDetailId;
    private String userName;
    private String userAvatarColor;
    private String bookName;
    private String barcode;
    private String borrowDate;
    private String dueDate;
    private String returnDate;
    private String status;
    private String note;
}