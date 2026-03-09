package com.library.server.controller;

import com.library.server.entity.Book;
import com.library.server.entity.BookCopy;
import com.library.server.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@CrossOrigin("*")
public class BookController {

    @Autowired
    private BookService bookService;

    // Lấy toàn bộ danh sách sách  http://localhost:8080/api/v1/books
    @GetMapping
    public ResponseEntity<List<Book>> getAll() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    // Lấy chi tiết 1 cuốn sách: http://localhost:8080/api/v1/books/1
    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    // Tìm kiếm sách: http://localhost:8080/api/v1/books/search?title=Python
    @GetMapping("/search")
    public ResponseEntity<List<Book>> search(@RequestParam String title) {
        return ResponseEntity.ok(bookService.searchBooksByTitle(title));
    }

    // Lấy danh sách bản sao của 1 cuốn sách: http://localhost:8080/api/v1/books/1/copies
    @GetMapping("/{id}/copies")
    public ResponseEntity<List<BookCopy>> getCopies(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.getCopiesByBookId(id));
    }

    // Thêm mới: POST http://localhost:8080/api/v1/books
    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(book));
    }

    // Sửa: PUT http://localhost:8080/api/v1/books/2
    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@PathVariable Integer id, @RequestBody Book book) {
        return ResponseEntity.ok(bookService.updateBook(id, book));
    }

    // Xóa: DELETE http://localhost:8080/api/v1/books/2
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}