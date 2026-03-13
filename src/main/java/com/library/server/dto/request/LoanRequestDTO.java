package com.library.server.dto.request;

import lombok.Data;

@Data
public class LoanRequestDTO {
    private String userId;
    private String bookId;
    private String borrowDate;
    private String dueDate;
    private String note;
}