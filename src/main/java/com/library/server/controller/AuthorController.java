package com.library.server.controller;

import com.library.server.dto.response.AuthorResponseDTO;
import com.library.server.dto.request.AuthorRequestDTO;
import com.library.server.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
@CrossOrigin("*") // Cho phép các ứng dụng khác gọi API này
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    // 1. Lấy danh sách tác giả
    @GetMapping
    public ResponseEntity<List<AuthorResponseDTO>> list(@RequestParam(required = false) String name) {
        if (name != null && !name.isEmpty()) {
            return ResponseEntity.ok(authorService.search(name));
//        GET http://localhost:8080/api/v1/authors
        }
        return ResponseEntity.ok(authorService.getAllAuthors());
    }


    // 2. Lấy chi tiết một tác giả
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    // 3. Thêm mới tác giả
    @PostMapping
    public ResponseEntity<AuthorResponseDTO> create(@Valid @RequestBody AuthorRequestDTO requestDTO)  {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.createAuthor(requestDTO));
//        POST http://localhost:8080/api/v1/authors
    }

    // 4. Cập nhật tác giả
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody AuthorRequestDTO requestDTO) {
        return ResponseEntity.ok(authorService.updateAuthor(id, requestDTO));
    }

    // 5. Xóa tác giả
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}

