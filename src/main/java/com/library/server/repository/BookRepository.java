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
    // TÌm kiếm theo barcode
    List<Book> findByIsbnContainingIgnoreCase(String isbn);

    @Query(value = "SELECT b.title, a.name as authorName, COUNT(ld.id) as borrowCount " +
            "FROM loan_details ld " +
            "JOIN book_copies bc ON ld.book_copy_id = bc.id " +
            "JOIN books b ON bc.book_id = b.id " +
            "JOIN authors a ON b.author_id = a.id " +
            "GROUP BY b.id, b.title, a.name " +
            "ORDER BY borrowCount DESC " +
            "LIMIT 4", nativeQuery = true)
    List<Object[]> getTopPopularBooks();
}