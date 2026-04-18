package com.library.server.repository;

import com.library.server.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Tìm kiếm gần đúng theo tên, không phân biệt hoa thường
    List<Category> findByNameContainingIgnoreCase(String name);
}