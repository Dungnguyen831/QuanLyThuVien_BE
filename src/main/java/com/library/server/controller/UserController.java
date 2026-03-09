package com.library.server.controller;

import com.library.server.dto.response.UserResponseDTO;
import com.library.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users") // Đường dẫn gốc cho các API liên quan đến user
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // API 1: Lấy tất cả người dùng (Method: GET)
    // Đường dẫn: http://localhost:8080/api/users
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // API 2: Lấy 1 người dùng theo ID (Method: GET)
    // Đường dẫn ví dụ: http://localhost:8080/api/users/1
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        try {
            UserResponseDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}