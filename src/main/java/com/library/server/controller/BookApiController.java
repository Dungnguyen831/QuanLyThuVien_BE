package com.library.server.controller;

import com.library.server.entity.Book;
import com.library.server.entity.BookCopy;
import com.library.server.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@CrossOrigin("*") // Cho phép Frontend gọi API
public class BookApiController {

    @Autowired
    private BookService bookService;

    // GET: http://localhost:8080/api/books
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    // GET: http://localhost:8080/api/books/1
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    // GET: http://localhost:8080/api/books/search?title=java
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String title) {
        return ResponseEntity.ok(bookService.searchBooks(title));
    }

    // 1. Lấy toàn bộ bản sao có trong thư viện
    // GET: http://localhost:8080/api/v1/books/copies
    @GetMapping("/copies")
    public ResponseEntity<List<BookCopy>> getAllCopies() {
        return ResponseEntity.ok(bookService.getAllCopies());
    }

    // 2. Lấy các bản sao của một cuốn sách cụ thể theo ID sách
    // GET: http://localhost:8080/api/v1/books/1/copies
    @GetMapping("/{id}/copies")
    public ResponseEntity<List<BookCopy>> getCopiesByBookId(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.getCopiesByBookId(id));
    }
}