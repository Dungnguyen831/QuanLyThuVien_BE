package com.library.server.controller;

import com.library.server.dto.request.BookRequestDTO; // Import DTO để hứng dữ liệu POST
import com.library.server.entity.Book;
import com.library.server.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@CrossOrigin(origins = "*") // Cho phép Frontend gọi API từ port khác (ví dụ 5500)
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * API Lấy danh sách sách: GET http://localhost:8080/api/v1/books
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAll() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /**
     * API Thêm mới sách: POST http://localhost:8080/api/v1/books
     * Chú ý: Sử dụng @RequestBody BookRequestDTO thay vì Book Entity trực tiếp.
     */
    @PostMapping
    public ResponseEntity<Book> create(@RequestBody BookRequestDTO bookRequestDTO) {
        // Gọi Service xử lý logic chuyển đổi ID sang Object và lưu vào DB
        Book createdBook = bookService.createBook(bookRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    /**
     * API Cập nhật sách: PUT http://localhost:8080/api/v1/books/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@PathVariable Integer id, @RequestBody BookRequestDTO bookRequestDTO) {
        Book updatedBook = bookService.updateBook(id, bookRequestDTO);
        return ResponseEntity.ok(updatedBook);
    }

    /**
     * API Xóa sách: DELETE http://localhost:8080/api/v1/books/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}