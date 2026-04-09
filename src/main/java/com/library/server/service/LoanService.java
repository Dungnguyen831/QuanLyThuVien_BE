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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanDetailRepository loanDetailRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    private static final String[] AVATAR_COLORS = {"#FF5733", "#33FF57", "#3357FF", "#F033FF", "#33FFF0"};



    // Format ngày giờ cho đẹp trước khi gửi sang PHP
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<LoanResponseDTO> getAllLoansForDashboard() {
        // 1. Kéo toàn bộ chi tiết mượn từ DB lên
        List<LoanDetail> details = loanDetailRepository.findAll();

        // 2. Chuyển đổi (Map) từng dòng Entity sang DTO
        return details.stream().map(detail -> {

            String realStatus = "borrowing";
            if (detail.getReturnDate() != null) {
                realStatus = "returned";
            } else if (detail.getDueDate() != null && detail.getDueDate().isBefore(LocalDateTime.now())) {
                realStatus = "overdue";
            }

            String fullName = detail.getLoan().getUser().getFullName();
            String title = detail.getBookCopy().getBook().getTitle();

            return LoanResponseDTO.builder()
                    .id("MP" + String.format("%03d", detail.getLoan().getId()))
                    .userName(fullName)
                    .userAvatarColor("#0d6efd")
                    .bookName(title)
                    .borrowDate(detail.getLoan().getBorrowDate().format(formatter))
                    .dueDate(detail.getDueDate().format(formatter))
                    .returnDate(detail.getReturnDate() != null ? detail.getReturnDate().format(formatter) : "-")
                    .status(realStatus)
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

    public LoanResponseDTO getLoanById(Integer id) {
        // 1. Tìm phiếu mượn
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn ID: " + id));

        // 2. Tìm người mượn
        User user = userRepository.findById(loan.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy độc giả!"));

        // 3. Tìm chi tiết mượn (Lấy cuốn sách đầu tiên trong phiếu)
        LoanDetail detail = loanDetailRepository.findById(loan.getId()).stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Phiếu mượn này chưa có sách nào!"));

        // 4. Tìm sách vật lý (Book Copy) và thông tin sách gốc (Book)
        BookCopy bookCopy = bookCopyRepository.findById(detail.getBookCopy().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản sao sách!"));
        Book book = bookRepository.findById(bookCopy.getBook().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đầu sách!"));

        // 5. Sinh màu ngẫu nhiên cho Avatar
        String randomColor = AVATAR_COLORS[new Random().nextInt(AVATAR_COLORS.length)];

        // 6. Đóng gói vào chuẩn DTO của bạn
        return LoanResponseDTO.builder()
                .id("MP" + String.format("%03d", loan.getId())) // Tạo mã MP001, MP002...
                .userName(user.getFullName())
                .userAvatarColor(randomColor)
                .bookName(book.getTitle())
                .borrowDate(loan.getBorrowDate().toString().substring(0, 10)) // Cắt lấy YYYY-MM-DD
                .dueDate(detail.getDueDate().toString().substring(0, 10))
                .returnDate(detail.getReturnDate() != null ? detail.getReturnDate().toString().substring(0, 10) : "")
                .status(detail.getStatus())
                .build();
    }

    public List<LoanResponseDTO> getLoansByUserId(Integer userId) {
        // 1. Lấy toàn bộ phiếu mượn của user này từ Database
        List<Loan> loans = loanRepository.findByUserId(userId);

        // 2. Tạo một danh sách rỗng để chứa kết quả DTO
        List<LoanResponseDTO> resultList = new ArrayList<>();

        // 3. Lặp qua từng phiếu mượn và dùng lại hàm getLoanById để "chế biến" dữ liệu
        for (Loan loan : loans) {
            try {
                // Tái sử dụng hàm lấy chi tiết 1 phiếu mượn bạn đã viết lúc nãy!
                LoanResponseDTO dto = getLoanById(loan.getId());
                resultList.add(dto);
            } catch (RuntimeException e) {
                // Dùng try-catch để nếu có 1 phiếu bị lỗi dữ liệu (ví dụ mượn mà chưa có sách),
                // hệ thống sẽ bỏ qua phiếu đó và vẫn load tiếp các phiếu bình thường, không bị sập mạng.
                System.out.println("Bỏ qua phiếu mượn bị lỗi ID: " + loan.getId());
            }
        }

        return resultList;
    }
}