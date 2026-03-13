package com.library.server.service;

import com.library.server.dto.request.LoanRequestDTO;
import com.library.server.dto.response.LoanResponseDTO;
import com.library.server.entity.*;
import com.library.server.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanDetailRepository loanDetailRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookCopyRepository bookCopyRepository;


    // Format ngày giờ cho đẹp trước khi gửi sang PHP
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<LoanResponseDTO> getAllLoansForDashboard() {
        // 1. Kéo toàn bộ chi tiết mượn từ DB lên
        List<LoanDetail> details = loanDetailRepository.findAll();

        // 2. Chuyển đổi (Map) từng dòng Entity sang DTO
        return details.stream().map(detail -> {
            // Lấy tên User (xuyên qua bảng loans)
            String fullName = detail.getLoan().getUser().getFullName();

            // Lấy tên Sách (xuyên qua bảng book_copies -> books)
            String title = detail.getBookCopy().getBook().getTitle();

            return LoanResponseDTO.builder()
                    .id("MP00" + detail.getLoan().getId()) // Giả lập mã phiếu MP001
                    .userName(fullName)
                    .userAvatarColor("#0d6efd") // Tạm fix cứng 1 màu xanh
                    .bookName(title)
                    .borrowDate(detail.getLoan().getBorrowDate().format(formatter))
                    .dueDate(detail.getDueDate().format(formatter))
                    .returnDate(detail.getReturnDate() != null ? detail.getReturnDate().format(formatter) : "-")
                    .status(detail.getStatus())
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional // Đảm bảo nếu lỗi giữa chừng thì sẽ rollback (hủy) toàn bộ
    public void createNewLoan(LoanRequestDTO request) {
        // 1. Lọc lấy ID số (VD: "US001" -> 1, "BK012" -> 12)
        Integer userId = extractId(request.getUserId());
        Integer bookId = extractId(request.getBookId());

        // 2. Kiểm tra User có tồn tại không
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy độc giả với mã: " + request.getUserId()));

        // 3. Tìm 1 bản sao của sách (BookCopy) đang rảnh
        // Vì hệ thống lưu mượn theo từng Cuốn (Copy), ta lấy đại 1 bản rảnh của đầu sách đó
        List<BookCopy> availableCopies = bookCopyRepository.findByBookId(bookId);
        if (availableCopies.isEmpty()) {
            throw new RuntimeException("Đầu sách này hiện không còn cuốn nào trong kho!");
        }
        BookCopy copyToBorrow = availableCopies.get(0); // Lấy cuốn đầu tiên tìm thấy

        // 4. Tạo hóa đơn mượn (Bảng loans)
        Loan loan = new Loan();
        loan.setUser(user);
        // Chuyển chuỗi "2026-03-10" sang LocalDateTime (00:00:00)
        loan.setBorrowDate(LocalDate.parse(request.getBorrowDate()).atStartOfDay());
        loan.setNote(request.getNote());

        loan = loanRepository.save(loan); // Lưu bảng cha trước để lấy ID

        // 5. Tạo chi tiết mượn (Bảng loan_details)
        LoanDetail detail = new LoanDetail();
        detail.setLoan(loan);
        detail.setBookCopy(copyToBorrow);
        // Hẹn trả vào 23:59:59 của ngày trả
        detail.setDueDate(LocalDate.parse(request.getDueDate()).atTime(23, 59, 59));
        detail.setStatus("borrowing");

        loanDetailRepository.save(detail);
    }

    // Hàm phụ trợ: Lọc bỏ chữ, chỉ lấy số từ chuỗi
    private Integer extractId(String input) {
        if (input == null || input.isEmpty()) return null;
        String numberOnly = input.replaceAll("\\D+", ""); // Xóa mọi ký tự không phải số
        if (numberOnly.isEmpty()) throw new RuntimeException("Mã không hợp lệ: " + input);
        return Integer.parseInt(numberOnly);
    }
}