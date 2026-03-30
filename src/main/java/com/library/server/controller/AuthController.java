package com.library.server.controller;

import com.library.server.dto.request.RegisterRequestDTO;
import com.library.server.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        try {
            String result = authService.register(request);
            // Tạo một Map để Spring chuyển thành JSON
            Map<String, String> response = new HashMap<>();
            response.put("message", result);
            return ResponseEntity.ok(response); // Trả về {"message": "Đăng ký tài khoản thành công!"}
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse); // Trả về {"error": "Lý do lỗi"}
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody com.library.server.dto.request.LoginRequestDTO request) {
        try {
            com.library.server.dto.response.LoginResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response); // Đã là đối tượng DTO nên Spring tự động chuyển thành JSON
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}