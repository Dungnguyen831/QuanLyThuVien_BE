package com.library.server.repository;

import com.library.server.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {
    // Tìm danh sách bản sao dựa trên book_id
    List<BookCopy> findByBookId(Integer bookId);

}