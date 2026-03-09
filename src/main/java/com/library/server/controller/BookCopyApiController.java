package com.library.server.controller;

import com.library.server.entity.BookCopy;
import com.library.server.service.BookCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/book-copies")
@CrossOrigin("*")
public class BookCopyApiController {

    @Autowired
    private BookCopyService bookCopyService;

    // Tạo bản sao: POST http://localhost:8080/api/v1/book-copies
    @PostMapping
    public ResponseEntity<BookCopy> create(@RequestBody BookCopy copy) {
        return new ResponseEntity<>(bookCopyService.createCopy(copy), HttpStatus.CREATED);
    }

    // Xóa bản sao: DELETE http://localhost:8080/api/v1/book-copies/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        bookCopyService.deleteCopy(id);
        return ResponseEntity.noContent().build();
    }
}