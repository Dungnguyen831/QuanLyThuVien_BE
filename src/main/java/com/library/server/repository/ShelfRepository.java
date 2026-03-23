package com.library.server.repository;

import com.library.server.entity.Publisher;
import com.library.server.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Integer> {
    List<Shelf> findByNameContainingIgnoreCase(String name);
    // Tự động hỗ trợ các hàm CRUD cơ bản
}