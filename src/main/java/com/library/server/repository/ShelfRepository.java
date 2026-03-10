package com.library.server.repository;

import com.library.server.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Integer> {
    // Tự động hỗ trợ các hàm CRUD cơ bản
}