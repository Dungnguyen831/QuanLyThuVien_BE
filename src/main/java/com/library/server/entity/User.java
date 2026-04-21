package com.library.server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id") // Ánh xạ đúng với cột role_id và khóa ngoại fk_user_role dưới DB
    private Role role;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "status") // Trong DB mới, status đang là varchar(255)
    private String status;

    @Column(name = "msv", length = 50)
    private String msv;

    // --- CÁC HÀM CỦA USERDETAILS CẦN THIẾT CHO SPRING SECURITY ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Cấp quyền cho user dựa vào tên Role (VD: "ADMIN", "USER")
        return List.of(new SimpleGrantedAuthority(role.getName()));
    }

    @Override
    public String getUsername() {
        // Dùng email làm tên đăng nhập
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Tài khoản không bị khóa nếu status khác INACTIVE
        return !"INACTIVE".equalsIgnoreCase(status);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Tài khoản được kích hoạt nếu status là ACTIVE
        return "ACTIVE".equalsIgnoreCase(status);
    }
}