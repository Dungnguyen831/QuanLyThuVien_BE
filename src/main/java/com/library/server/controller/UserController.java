package com.library.server.controller;

import com.library.server.dto.request.ChangePasswordDTO;
import com.library.server.dto.request.UserRequestDTO;
import com.library.server.dto.response.UserResponseDTO;
import com.library.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users") // Đường dẫn gốc cho các API liên quan đến user
@CrossOrigin("*") // Cho phép frontend gọi API
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. Lấy danh sách (Có thể lọc theo role và search)
    // Ví dụ: GET /api/v1/users?role=user&keyword=nguyen
    //@PreAuthorize("hasAnyRole('admin')")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword) {
        
        if (role != null && !role.isEmpty()) {
            return ResponseEntity.ok(userService.getUsersByRoleAndKeyword(role, keyword));
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 2. Lấy 1 người dùng theo ID (Method: GET)
    // Đường dẫn ví dụ: http://localhost:8080/api/v1/users/1
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        try {
            UserResponseDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    // 3. Thêm mới độc giả/người dùng
    // POST /api/v1/users
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequestDTO requestDTO) {
        try {
            UserResponseDTO newUser = userService.createUser(requestDTO);
            return ResponseEntity.ok(newUser);
        } catch (RuntimeException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }


    // 4. Cập nhật thông tin độc giả
    // PUT /api/v1/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody UserRequestDTO requestDTO) {
        try {
            UserResponseDTO updatedUser = userService.updateUser(id, requestDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }



    // 5. Khóa/Mở khóa độc giả
    // PATCH /api/v1/users/{id}/status?status=INACTIVE
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(
            @PathVariable Integer id, 
            @RequestParam String status) {
        try {
            UserResponseDTO updatedUser = userService.changeStatus(id, status);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    // 6. Xóa người dùng
    // DELETE /api/v1/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    // 7. Đổi mật khẩu (Cần kiểm tra mật khẩu cũ)
    // PATCH /api/v1/users/{id}/password
    @PatchMapping("/{id}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable Integer id,
            @RequestBody ChangePasswordDTO requestDTO) { // Sinh ra 1 cái DTO mới chỉ chứa 2 trường: oldPassword, newPassword
        try {
            // Gọi Service xuống kiểm tra và cập nhật
            userService.changePassword(id, requestDTO);

            // Trả về thông báo thành công cho Frontend
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đổi mật khẩu thành công!");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Nếu Service ném lỗi (Ví dụ: "Mật khẩu cũ không đúng"), trả về lỗi 400
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }
}