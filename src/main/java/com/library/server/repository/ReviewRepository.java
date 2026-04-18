package com.library.server.repository;

import com.library.server.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // Cách 2: Custom Query - Lấy review theo bookId
    @Query("SELECT r FROM Review r WHERE r.book.id = :bookId ORDER BY r.createdAt DESC")
    List<Review> findByBookId(@Param("bookId") Integer bookId);

    // Alternative: Có thể sử dụng derived query (Spring tự generate SQL)
    // List<Review> findByBook_IdOrderByCreatedAtDesc(Integer bookId);
}

