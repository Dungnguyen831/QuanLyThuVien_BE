package com.library.server.repository;

import com.library.server.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    // Spring Data JPA sẽ tự động tạo câu lệnh SQL (SELECT * FROM roles WHERE name = ?)
    // Hàm này được dùng trong AuthService để tìm quyền mặc định ("READER")
    Optional<Role> findByName(String name);
}