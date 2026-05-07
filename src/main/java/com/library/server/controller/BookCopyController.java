package com.library.server.controller;

import com.library.server.dto.request.BookCopyRequestDTO;
import com.library.server.entity.BookCopy;
import com.library.server.service.BookCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/book-copies")
@CrossOrigin("*")
public class BookCopyController {

    @Autowired
    private BookCopyService bookCopyService;

    // Tạo bản sao: POST http://localhost:8080/api/v1/book-copies
    @PostMapping
    public ResponseEntity<BookCopy> create(@RequestBody BookCopy copy) {
        return new ResponseEntity<>(bookCopyService.createCopy(copy), HttpStatus.CREATED);
    }
    // tạo bản sao theo số lượng
    // API tạo hàng loạt bản sao (Bulk Create)
    // URL ví dụ: POST http://localhost:8080/api/v1/book-copies/bulk?quantity=5
    @PostMapping("/bulk")
    public ResponseEntity<List<BookCopy>> createBulk(
            @RequestBody BookCopy copyTemplate,
            @RequestParam int quantity) {

        List<BookCopy> result = bookCopyService.createMultipleCopiesManual(copyTemplate, quantity);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    // Xóa bản sao: DELETE http://localhost:8080/api/v1/book-copies/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        bookCopyService.deleteCopy(id);
        return ResponseEntity.noContent().build();
    }
    // Cập nhật bản sao: PUT http://localhost:8080/api/v1/book-copies/{id}
    @PutMapping("/{id}")
    public ResponseEntity<BookCopy> update(@PathVariable Integer id, @RequestBody BookCopyRequestDTO copy) {
        return ResponseEntity.ok(bookCopyService.updateCopy(id, copy));
    }
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BookCopy>> getByBookId(@PathVariable Integer bookId) {
        // Sử dụng hàm findByBookId đã có sẵn trong Repository của ông
        return ResponseEntity.ok(bookCopyService.getCopiesByBookId(bookId));
    }
}
