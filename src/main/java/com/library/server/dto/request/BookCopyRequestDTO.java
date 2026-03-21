package com.library.server.dto.request;
import lombok.Data;

@Data
public class BookCopyRequestDTO {
    private Integer bookId;
    private Integer shelfId;
    private String barcode;
    private String conditionStatus;
    private String availabilityStatus;
}