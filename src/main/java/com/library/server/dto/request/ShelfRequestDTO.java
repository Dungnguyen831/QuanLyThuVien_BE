package com.library.server.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;


@Data
public class ShelfRequestDTO{
    private String name;
    private Integer floor;
    private Integer categoryID;


}
