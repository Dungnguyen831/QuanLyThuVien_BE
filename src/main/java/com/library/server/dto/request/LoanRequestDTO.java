package com.library.server.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoanRequestDTO {

    @NotBlank
    private String userId;

    @NotBlank
    private String bookId;

    @NotBlank
    private String borrowDate;

    @NotBlank
    private String dueDate;
    private String barcode;

    private String note;
}