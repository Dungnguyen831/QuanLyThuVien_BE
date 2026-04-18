package com.library.server.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class PublisherRequestDTO {
    private String name;
    private String address;
    private String email;
}
