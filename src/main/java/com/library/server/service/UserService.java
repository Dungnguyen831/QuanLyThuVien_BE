package com.library.server.service;

import com.library.server.dto.response.UserResponseDTO;
import com.library.server.entity.User;
import com.library.server.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Hàm phụ trợ: Chuyển đổi từ User Entity sang UserResponseDTO để giấu mật khẩu
    private UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .roleName(user.getRole() != null ? user.getRole().getName() : "NO_ROLE")
                .build();
    }

    // Lấy danh sách TẤT CẢ người dùng
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll(); // JpaRepository đã viết sẵn hàm này

        // Chuyển đổi danh sách Entity thành danh sách DTO
        return users.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy thông tin MỘT người dùng theo ID
    public UserResponseDTO getUserById(Integer id) {
        User user = userRepository.findById(id) // JpaRepository đã viết sẵn hàm này
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
        return mapToDTO(user);
    }
}