package com.library.server.repository;

import com.library.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Spring Data JPA sẽ tự hiểu và tạo Query tìm kiếm User theo cột email
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    // Lưu ý: KHÔNG cần khai báo hàm save() ở đây vì nó đã nằm sẵn trong JpaRepository rồi.
}