package com.library.server.service;

import com.library.server.dto.request.UserRequestDTO;
import com.library.server.dto.response.UserResponseDTO;
import com.library.server.entity.Role;
import com.library.server.entity.User;
import com.library.server.repository.RoleRepository;
import com.library.server.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Hàm phụ trợ: Chuyển đổi từ User Entity sang UserResponseDTO để giấu mật khẩu
    private UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .msv(user.getMsv()) // BỔ SUNG: Trả về msv cho Frontend
                .status(user.getStatus())
                .roleName(user.getRole() != null ? user.getRole().getName() : "NO_ROLE")
                .build();
    }

    // Lấy TẤT CẢ người dùng
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // 1. Lấy danh sách người dùng theo Role và tìm kiếm (có thể null keyword)
    public List<UserResponseDTO> getUsersByRoleAndKeyword(String roleName, String keyword) {
        List<User> users = userRepository.findByRoleNameAndKeyword(roleName, keyword);
        return users.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy thông tin MỘT người dùng theo ID
    public UserResponseDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
        return mapToDTO(user);
    }

    // 2. Admin tạo người dùng mới
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng!");
        }
        if (requestDTO.getPhone() != null && !requestDTO.getPhone().trim().isEmpty()) {
            if (userRepository.findByPhone(requestDTO.getPhone()).isPresent()) {
                throw new RuntimeException("Số điện thoại này đã được đăng ký!");
            }
        }

        // BỔ SUNG: Kiểm tra xem MSV có bị trùng không
        if (requestDTO.getMsv() != null && !requestDTO.getMsv().trim().isEmpty()) {
            if (userRepository.findByMsv(requestDTO.getMsv().trim()).isPresent()) {
                throw new RuntimeException("Mã sinh viên này đã tồn tại trên hệ thống!");
            }
        }

        Role userRole = roleRepository.findByName(requestDTO.getRoleName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền: " + requestDTO.getRoleName()));

        User newUser = new User();
        newUser.setFullName(requestDTO.getFull_name());
        newUser.setEmail(requestDTO.getEmail());

        // BỔ SUNG: Lưu MSV
        if (requestDTO.getMsv() != null && !requestDTO.getMsv().trim().isEmpty()) {
            newUser.setMsv(requestDTO.getMsv().trim());
        }

        // Nếu admin không truyền password, có thể set mặc định (vd: 123456)
        String pass = (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty())
                ? requestDTO.getPassword() : "123456";
        newUser.setPassword(passwordEncoder.encode(pass));
        newUser.setPhone(requestDTO.getPhone());
        newUser.setRole(userRole);
        newUser.setStatus("ACTIVE");

        User savedUser = userRepository.save(newUser);
        return mapToDTO(savedUser);
    }

    // 3. Cập nhật thông tin người dùng
    public UserResponseDTO updateUser(Integer id, UserRequestDTO requestDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

        // Kiểm tra email trùng với người KHÁC
        if (!existingUser.getEmail().equals(requestDTO.getEmail())) {
            if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
                throw new RuntimeException("Email đã được sử dụng bởi tài khoản khác!");
            }
        }

        // Kiểm tra phone trùng
        if (requestDTO.getPhone() != null && !requestDTO.getPhone().isEmpty() && !requestDTO.getPhone().equals(existingUser.getPhone())) {
            if (userRepository.findByPhone(requestDTO.getPhone()).isPresent()) {
                throw new RuntimeException("Số điện thoại đã được sử dụng bởi tài khoản khác!");
            }
        }

        // BỔ SUNG: Kiểm tra MSV trùng với người KHÁC
        if (requestDTO.getMsv() != null && !requestDTO.getMsv().trim().isEmpty()) {
            Optional<User> userWithMsv = userRepository.findByMsv(requestDTO.getMsv().trim());
            if (userWithMsv.isPresent() && !userWithMsv.get().getId().equals(id)) {
                throw new RuntimeException("Mã sinh viên này đã được sử dụng bởi tài khoản khác!");
            }
            existingUser.setMsv(requestDTO.getMsv().trim());
        } else {
            existingUser.setMsv(null); // Nếu truyền rỗng thì set null
        }

        existingUser.setFullName(requestDTO.getFull_name());
        existingUser.setEmail(requestDTO.getEmail());
        existingUser.setPhone(requestDTO.getPhone());

        // Cập nhật mật khẩu nếu có
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }

        // Cập nhật quyền nếu có truyền lên
        if(requestDTO.getRoleName() != null && !requestDTO.getRoleName().trim().isEmpty()){
            Role role = roleRepository.findByName(requestDTO.getRoleName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền: " + requestDTO.getRoleName()));
            existingUser.setRole(role);
        }

        User updatedUser = userRepository.save(existingUser);
        return mapToDTO(updatedUser);
    }

    // 4. Khóa/Mở khóa tài khoản
    public UserResponseDTO changeStatus(Integer id, String newStatus) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

        if(!"ACTIVE".equalsIgnoreCase(newStatus) && !"INACTIVE".equalsIgnoreCase(newStatus)){
            throw new RuntimeException("Trạng thái không hợp lệ. Chỉ chấp nhận ACTIVE hoặc INACTIVE");
        }

        user.setStatus(newStatus.toUpperCase());
        User updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    // 5. Xóa người dùng
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
        userRepository.delete(user);
    }

    // 6. Đổi mật khẩu (dành cho User tự đổi, yêu cầu mật khẩu cũ)
    public void changePassword(Integer id, com.library.server.dto.request.ChangePasswordDTO requestDTO) {
        // 1. Lấy thông tin user từ DB
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

        // 2. Kiểm tra mật khẩu cũ có khớp với trong Database không
        // passwordEncoder.matches(chữ_thô_người_dùng_nhập, chuỗi_đã_băm_trong_DB)
        if (!passwordEncoder.matches(requestDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không chính xác!");
        }

        // 3. Nếu khớp -> Mã hóa mật khẩu mới và lưu lại
        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        userRepository.save(user);
    }

    // THÊM MỚI (Số 7): Cập nhật Mã sinh viên nhanh (Dành cho chức năng quét thẻ của Thủ thư)
    @Transactional
    public String updateMsv(Integer userId, String newMsv) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy độc giả này!"));

        // Kiểm tra xem MSV mới có bị trùng với người khác không
        if (newMsv != null && !newMsv.trim().isEmpty()) {
            Optional<User> existingUser = userRepository.findByMsv(newMsv.trim());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                throw new RuntimeException("Mã sinh viên này đã được cấp cho tài khoản khác!");
            }
        }
        user.setMsv(newMsv != null ? newMsv.trim() : null);
        userRepository.save(user);
        return "Cập nhật Mã sinh viên thành công!";
    }
}