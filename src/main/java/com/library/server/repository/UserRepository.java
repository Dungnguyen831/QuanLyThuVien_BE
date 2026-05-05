package com.library.server.repository;

import com.library.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Spring Data JPA sẽ tự hiểu và tạo Query tìm kiếm User theo cột email
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPhone(String phone);
    
    // Lấy danh sách theo Tên Role và tìm kiếm theo FullName (chứa từ khóa, không phân biệt hoa thường)
    @Query("SELECT u FROM User u WHERE u.role.name = :roleName " +
           "AND (:keyword IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> findByRoleNameAndKeyword(@Param("roleName") String roleName, @Param("keyword") String keyword);
    long countByCreatedAtAfter(LocalDateTime dateTime);
}