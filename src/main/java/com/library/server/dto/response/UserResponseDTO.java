package com.library.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    private String roleName;

    // Tuyệt đối KHÔNG khai báo biến password ở đây!
}