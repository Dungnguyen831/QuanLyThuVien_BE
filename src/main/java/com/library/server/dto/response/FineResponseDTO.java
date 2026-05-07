package com.library.server.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FineResponseDTO {
    private Integer id;
    private Integer userId;
    private String userName; // Lấy thêm tên để hiển thị cho đẹp
    private Integer loanDetailId;
    private String bookTitle; // Tên sách bị phạt
    private BigDecimal amount;
    private String reason;
    private Boolean isPaid;
    private LocalDateTime createdAt;

}