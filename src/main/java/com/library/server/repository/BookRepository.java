package com.library.server.repository;

import com.library.server.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    // Tìm kiếm theo tiêu đề (Like %title%)
    List<Book> findByTitleContainingIgnoreCase(String title);
}