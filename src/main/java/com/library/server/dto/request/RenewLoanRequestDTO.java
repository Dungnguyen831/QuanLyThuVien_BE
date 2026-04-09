package com.library.server.dto.request;
import lombok.Data;

@Data
public class RenewLoanRequestDTO {
    private String newDueDate; // Định dạng YYYY-MM-DD
}