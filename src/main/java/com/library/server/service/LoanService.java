package com.library.server.service;

import com.library.server.dto.request.LoanRequestDTO;
import com.library.server.dto.response.LoanResponseDTO;
import com.library.server.entity.BookCopy;
import com.library.server.entity.Loan;
import com.library.server.entity.LoanDetail;
import com.library.server.entity.User;
import com.library.server.repository.LoanDetailRepository;
import com.library.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanDetailRepository loanDetailRepository;
//    private final UserRepository userRepository;


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

//    @Transactional
//    public void createNewLoan(LoanRequestDTO request) {
//
//        // 1. Tìm Độc giả (User) dưới DB
//        User user = userRepository.findById(request.getUserId())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy độc giả với ID: " + request.getUserId()));
//
//        // 2. Tìm cuốn sách qua Mã vạch (Barcode)
//        // Giả sử BookCopyRepository có hàm: Optional<BookCopy> findByBarcode(String barcode);
//        BookCopy bookCopy = bookCopyRepository.findByBarcode(request.getBarcode())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã vạch sách: " + request.getBarcode()));
//
//        // Kiểm tra xem sách có sẵn để mượn không
//        if (!"AVAILABLE".equals(bookCopy.getAvailabilityStatus())) {
//            throw new RuntimeException("Cuốn sách này đang không có sẵn (đã mất hoặc đang cho mượn)!");
//        }
//
//        // 3. TẠO BẢN GHI VÀO BẢNG 'loans' (Phiếu gốc)
//        Loan newLoan = new Loan();
//        newLoan.setUser(user);
//        newLoan.setBorrowDate(LocalDateTime.now());
//        // (Bạn có thể set thêm note vào đây nếu entity Loan có trường note)
//
//        // Lưu bảng 'loans' trước để lấy ID
//        Loan savedLoan = loanRepository.save(newLoan);
//
//        // 4. TẠO BẢN GHI VÀO BẢNG 'loan_details' (Chi tiết cuốn sách được mượn)
//        LoanDetail detail = new LoanDetail();
//        detail.setLoan(savedLoan); // Nối với ID phiếu gốc vừa tạo
//        detail.setBookCopy(bookCopy); // Nối với cuốn sách
//
//        // Tính ngày hạn trả (Ngày hiện tại + số ngày mượn)
//        int days = (request.getDaysToBorrow() != null) ? request.getDaysToBorrow() : 14;
//        detail.setDueDate(LocalDateTime.now().plusDays(days));
//
//        detail.setStatus("borrowing");
//
//        loanDetailRepository.save(detail);
//
//        // 5. Cập nhật lại trạng thái cuốn sách thành "Đang mượn"
//        bookCopy.setAvailabilityStatus("BORROWED");
//        bookCopyRepository.save(bookCopy);
//
//        System.out.println("=> Ghi Database 2 bảng thành công!");
//    }
}