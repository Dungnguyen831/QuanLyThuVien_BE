package com.library.server.controller;

import com.library.server.dto.request.LoanRequestDTO;
import com.library.server.dto.response.LoanResponseDTO;
import com.library.server.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@CrossOrigin(origins = "*") // Chống lỗi CORS
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @GetMapping
    public List<LoanResponseDTO> getLoans() {
        // Gọi Service lấy dữ liệu thật
        return loanService.getAllLoansForDashboard();
    }

    @PostMapping
    public ResponseEntity<String> createLoan(@RequestBody @Valid LoanRequestDTO request) {
        try {
            loanService.createNewLoan(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Tạo phiếu mượn thành công!");
        } catch (Exception e) {
            // Nếu có lỗi (Sai ID, sách hết...), ném thông báo lỗi về Frontend
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponseDTO> getLoanById(@PathVariable Integer id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getLoansByUserId(@PathVariable Integer userId) {
        try {
            List<LoanResponseDTO> loans = loanService.getLoansByUserId(userId);

            // Nếu danh sách trống thì báo lỗi nhẹ nhàng
            if (loans.isEmpty()) {
                return ResponseEntity.ok("Độc giả này chưa có phiếu mượn nào.");
            }

            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi lấy danh sách: " + e.getMessage());
        }
    }
}