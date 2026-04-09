package com.library.server.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // <--- CÁI NÀY LÀ QUAN TRỌNG NHẤT
public class AuthorRequestDTO {
    private String name;
    private String biography;
    private String country;
    private Integer bookcount;
    private String status;

}