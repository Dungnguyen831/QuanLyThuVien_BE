package com.library.server.controller;

import com.library.server.dto.request.RegisterRequestDTO;
import com.library.server.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            return ResponseEntity.ok(result); // Trả về HTTP 200 OK nếu thành công
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Trả về HTTP 400 Bad Request nếu lỗi (VD: trùng email)
        }
    }

    // ... (Giữ nguyên hàm register cũ)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody com.library.server.dto.request.LoginRequestDTO request) {
        try {
            com.library.server.dto.response.LoginResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response); // Trả về thông tin user và HTTP status 200
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Trả về lỗi và HTTP status 400
        }
    }
}