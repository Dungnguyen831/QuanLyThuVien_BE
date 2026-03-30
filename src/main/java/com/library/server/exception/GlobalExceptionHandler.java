package com.library.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // Đánh dấu đây là Trạm kiểm soát lỗi toàn cục
public class GlobalExceptionHandler {

    // Bắt đúng cái lỗi xảy ra khi @Valid phát hiện gửi thiếu data
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {

        // Trả về đúng 1 câu báo lỗi gọn gàng như bạn muốn
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Định dạng JSON không khớp hoặc bạn đang gửi thiếu dữ liệu bắt buộc!");

        /* * (Nâng cao) Nếu bạn muốn chỉ rõ cụ thể thiếu cái gì, bạn có thể dùng đoạn code này thay thế:
         * String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
         * return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi: " + errorMessage);
         */
    }
}