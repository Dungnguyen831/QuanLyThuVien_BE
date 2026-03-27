package com.library.server.service;

import com.library.server.dto.request.RegisterRequestDTO;
import com.library.server.entity.Role;
import com.library.server.entity.User;
import com.library.server.repository.RoleRepository;
import com.library.server.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    // Inject các dependency thông qua Constructor (đã loại bỏ PasswordEncoder)
    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String register(RegisterRequestDTO request) {
        // 1. Kiểm tra xem Email đã tồn tại chưa
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng. Vui lòng chọn email khác!");
        }

        // 2. Lấy Role mặc định từ DB
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Không tìm thấy quyền mặc định (USER)."));

        // 3. Khởi tạo đối tượng User mới
        User newUser = new User();
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        newUser.setPhone(request.getPhone());
        newUser.setRole(userRole);
        newUser.setStatus("ACTIVE");
        // 4. Lưu xuống CSDL
        userRepository.save(newUser);
        return "Đăng ký tài khoản thành công!";
    }

    public com.library.server.dto.response.LoginResponseDTO login(com.library.server.dto.request.LoginRequestDTO request) {
        // 1. Tìm User trong Database theo Email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        // 2. So sánh mật khẩu (Đang so sánh chuỗi thuần túy vì không dùng mã hóa)
        // So sánh mật khẩu nhập với hash đã lưu trong DB
        // passwordEncoder.matches(plainPassword, hashedPassword)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hoặc tài khoản không chính xác!");
        }
        // 3. Kiểm tra trạng thái tài khoản (Tùy chọn: chặn user bị khóa)
        if ("INACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("Tài khoản của bạn đã bị khóa!");
        }
        // 4. Trả về thông tin cho người dùng nếu đăng nhập thành công
        return new com.library.server.dto.response.LoginResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole() != null ? user.getRole().getName() : "NO_ROLE",
                "Đăng nhập thành công!"
        );
    }
}