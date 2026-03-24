package com.library.server.dto.request;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String full_name;
    private String email;
    private String phone;
    private String password; // Dùng khi tạo mới (nếu cần) hoặc khi muốn đổi mật khẩu
    private String roleName; // Tên quyền, vd: "user", "admin"
}
