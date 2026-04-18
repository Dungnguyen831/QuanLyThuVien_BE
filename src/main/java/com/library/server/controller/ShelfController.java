package com.library.server.controller;

import com.library.server.dto.request.ShelfRequestDTO;
import com.library.server.dto.response.ShelfResponseDTO;
import com.library.server.service.ShelfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shelves")
@CrossOrigin("*")
@RequiredArgsConstructor // Sử dụng cái này thay cho @Autowired để đúng chuẩn hiện đại
public class ShelfController {

    private final ShelfService shelfService;

    // 1. Lấy danh sách hoặc tìm kiếm
    @GetMapping
    public ResponseEntity<List<ShelfResponseDTO>> list(@RequestParam(required = false) String name) {
        if (name != null && !name.isEmpty()) {
            // Nếu bạn chưa viết hàm search trong Service, hãy dùng tạm getAllShelves()
            // hoặc xem hàm search bổ sung bên dưới
            return ResponseEntity.ok(shelfService.search(name));
        }
        return ResponseEntity.ok(shelfService.getAllShelves());
    }

    // 2. Lấy 1 kệ theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ShelfResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(shelfService.getShelfById(id));
    }

    // 3. Thêm mới kệ sách
    @PostMapping
    public ResponseEntity<ShelfResponseDTO> create(@RequestBody ShelfRequestDTO shelfRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(shelfService.createShelf(shelfRequestDTO));
    }

    // 4. Cập nhật kệ sách
    @PutMapping("/{id}")
    public ResponseEntity<ShelfResponseDTO> update(@PathVariable Integer id, @RequestBody ShelfRequestDTO shelfRequestDTO) {
        return ResponseEntity.ok(shelfService.updateShelf(id, shelfRequestDTO));
    }

    // 5. Xóa kệ sách
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        shelfService.deleteShelf(id);
        return ResponseEntity.noContent().build();
    }
}