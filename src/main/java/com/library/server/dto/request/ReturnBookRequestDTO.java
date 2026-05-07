package com.library.server.dto.request;

import lombok.Data;

@Data
public class ReturnBookRequestDTO {
    // Tình trạng sách khi trả: "GOOD" (Tốt), "DAMAGED" (Hư hỏng), "LOST" (Mất)
    private String inputBarcode;
    private String conditionStatus;

    // Ghi chú thêm nếu sách bị hỏng (Tùy chọn)
    private String note;
}