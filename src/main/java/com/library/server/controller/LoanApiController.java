package com.library.server.controller;

import com.library.server.dto.request.LoanRequestDTO;
import com.library.server.dto.response.LoanResponseDTO;
import com.library.server.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@CrossOrigin(origins = "*") // Chống lỗi CORS
@RequiredArgsConstructor
public class LoanApiController {
    private final LoanService loanService;

    @GetMapping
    public List<LoanResponseDTO> getLoans() {
        // Gọi Service lấy dữ liệu thật
        return loanService.getAllLoansForDashboard();
    }

//    public String createLoan(@RequestBody LoanRequestDTO request) {
//        try {
//            // Lấy thông tin ra để ktra xem có nhận đúng chưa
//            System.out.println("--- ĐANG TẠO PHIẾU MƯỢN MỚI ---");
//            System.out.println("UserID: " + request.getUserId());
//            System.out.println("Mã sách (Barcode): " + request.getBarcode());
//            System.out.println("Số ngày mượn: " + request.getDaysToBorrow());
//
//            // Gọi hàm của Service để lưu thật vào DB (Bảng loans & loan_details)
//            loanService.createNewLoan(request);
//
//            return ResponseEntity.status(HttpStatus.CREATED).body("Tạo phiếu mượn thành công!");
//
//        } catch (Exception e) {
//            // Nếu lỗi (ví dụ sách đã bị mượn, sai ID...), trả về lỗi 400
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi: " + e.getMessage());
//        }
//    }
}