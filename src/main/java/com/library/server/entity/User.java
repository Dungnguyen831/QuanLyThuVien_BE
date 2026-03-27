package com.library.server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
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
}