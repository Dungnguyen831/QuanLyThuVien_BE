package com.library.server.repository;

import com.library.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

    // 1. THÊM MỚI: Dùng để kiểm tra xem MSV có bị trùng với người khác không
    Optional<User> findByMsv(String msv);

    // 2. NÂNG CẤP: Cho phép tìm kiếm theo Tên, SĐT hoặc Mã sinh viên
    @Query("SELECT u FROM User u WHERE u.role.name = :roleName " +
            "AND (:keyword IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.msv) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> findByRoleNameAndKeyword(@Param("roleName") String roleName, @Param("keyword") String keyword);
}