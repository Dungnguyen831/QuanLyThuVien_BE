package com.library.server.dto.request;

import lombok.Data;

@Data
public class LoanRequestDTO {
    private Integer userId;
    private String barcode;
    private Integer daysToBorrow;
    private String note;
}