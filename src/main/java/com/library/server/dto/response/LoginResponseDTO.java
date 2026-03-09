package com.library.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private Integer userId;
    private String email;
    private String fullName;
    private String roleName;
    private String message;
}