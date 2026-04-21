package com.library.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    // Chỉ lấy tên Role (VD: "ADMIN", "USER") trả về cho Frontend
    private String status;
    private String msv;
}