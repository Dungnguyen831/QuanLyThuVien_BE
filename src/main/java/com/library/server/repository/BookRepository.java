package com.library.server.repository;

import com.library.server.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    // Tìm kiếm sách theo tiêu đề (chứa từ khóa, không phân biệt hoa thường)
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Tìm sách theo danh mục
    List<Book> findByCategoryId(Integer categoryId);

    // Tìm sách theo tác giả
    List<Book> findByAuthorId(Integer authorId);
}