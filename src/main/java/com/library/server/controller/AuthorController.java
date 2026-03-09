package com.library.server.controller;

import com.library.server.entity.Author;
import com.library.server.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
@CrossOrigin("*") // Cho phép các ứng dụng khác gọi API này
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    // 1. Lấy danh sách tác giả
    @GetMapping
    public List<Author> list() {
        return authorService.getAllAuthors();
//        GET http://localhost:8080/api/v1/authors
    }

    // 2. Lấy chi tiết một tác giả
    @GetMapping("/{id}")
    public ResponseEntity<Author> getById(@PathVariable Integer id) {
        return authorService.getAuthorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Thêm mới tác giả
    @PostMapping
    public Author create(@RequestBody Author author) {
        return authorService.saveAuthor(author);
//        POST http://localhost:8080/api/v1/authors
    }

    // 4. Cập nhật tác giả
    @PutMapping("/{id}")
    public ResponseEntity<Author> update(@PathVariable Integer id, @RequestBody Author authorDetails) {
        return authorService.getAuthorById(id).map(author -> {
            author.setName(authorDetails.getName());
            author.setBiography(authorDetails.getBiography());
            return ResponseEntity.ok(authorService.saveAuthor(author));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. Xóa tác giả
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.ok().build();
    }
}

