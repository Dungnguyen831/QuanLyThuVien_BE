package com.library.server.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class FineRequestDTO {
    @NotNull(message = "Thiếu mã chi tiết phiếu mượn")
    private Integer loanDetailId;

    @NotNull(message = "Thiếu số tiền phạt")
    @Min(value = 0, message = "Số tiền phạt không được âm")
    private BigDecimal amount;

    @NotBlank(message = "Thiếu lý do phạt")
    private String reason;
}