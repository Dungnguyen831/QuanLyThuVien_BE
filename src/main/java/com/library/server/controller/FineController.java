package com.library.server.controller;

import com.library.server.dto.request.FineRequestDTO;
import com.library.server.dto.response.FineResponseDTO;
import com.library.server.service.FineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fines")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FineController {

    private final FineService fineService;

    // Lấy danh sách tất cả khoản phạt
    @GetMapping
    public ResponseEntity<List<FineResponseDTO>> getAllFines() {
        return ResponseEntity.ok(fineService.getAllFines());
    }

    // Tạo phiếu phạt thủ công
    @PostMapping
    public ResponseEntity<String> createFine(@RequestBody @Valid FineRequestDTO request) {
        try {
            fineService.createManualFine(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Đã tạo biên bản phạt thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // Thu tiền phạt
    @PutMapping("/{id}/pay")
    public ResponseEntity<String> payFine(@PathVariable Integer id) {
        try {
            fineService.payFine(id);
            return ResponseEntity.ok("Xác nhận thu tiền thành công! Độc giả đã hoàn tất công nợ.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi thu tiền: " + e.getMessage());
        }
    }
}