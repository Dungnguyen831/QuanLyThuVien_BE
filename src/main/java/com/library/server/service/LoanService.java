package com.library.server.service;

import com.library.server.dto.request.LoanRequestDTO;
import com.library.server.dto.request.RenewLoanRequestDTO;
import com.library.server.dto.request.ReturnBookRequestDTO;
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
    private final FineRepository fineRepository;
    private final ReservationRepository reservationRepository;

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

            // Lấy tên Sách (xuyên qua bảng book_copies -> books)
            String title = detail.getBookCopy().getBook().getTitle();

            return LoanResponseDTO.builder()
                    .id("MP" + String.format("%03d", detail.getLoan().getId()))
                    .loanDetailId(detail.getId())
                    .userName(fullName)
                    .userAvatarColor("#0d6efd")
                    .bookName(title)
                    .barcode(detail.getBookCopy().getBarcode())
                    .borrowDate(detail.getLoan().getBorrowDate().format(formatter))
                    .dueDate(detail.getDueDate().format(formatter))
                    .returnDate(detail.getReturnDate() != null ? detail.getReturnDate().format(formatter) : "-")
                    .note(detail.getLoan().getNote())
                    .status(realStatus)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional // Đảm bảo nếu lỗi giữa chừng thì sẽ rollback (hủy) toàn bộ
    public void createNewLoan(LoanRequestDTO request) {

        Integer parsedUserId = Integer.parseInt(request.getUserId());
        long unpaidFines = fineRepository.countByUserIdAndIsPaidFalse(parsedUserId);
        if (unpaidFines > 0) {
            throw new RuntimeException("Độc giả đang có khoản phạt chưa thanh toán. Yêu cầu đóng phạt trước khi mượn sách mới!");
        }
        // 1. Lọc lấy ID số (VD: "US001" -> 1, "BK012" -> 12)
        Integer userId = extractId(request.getUserId());
        Integer bookId = extractId(request.getBookId());

        // 2. Kiểm tra User có tồn tại không
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy độc giả với mã: " + request.getUserId()));

        // 3. Tìm 1 bản sao của sách (BookCopy) đang rảnh
        // Vì hệ thống lưu mượn theo từng Cuốn (Copy), ta lấy đại 1 bản rảnh của đầu sách đó
        List<BookCopy> availableCopies = bookCopyRepository.findByBookIdAndAvailabilityStatus(bookId, "AVAILABLE");
        if (availableCopies.isEmpty()) {
            throw new RuntimeException("Đầu sách này hiện không còn cuốn nào trong kho!");
        }
        BookCopy copyToBorrow = availableCopies.get(0); // Lấy cuốn đầu tiên tìm thấy
        copyToBorrow.setAvailabilityStatus("UNAVAILABLE");
        bookCopyRepository.save(copyToBorrow);

        Book book = copyToBorrow.getBook();
        book.setAvailableQty(book.getAvailableQty() - 1);
        bookRepository.save(book);

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
        LoanDetail detail = loanDetailRepository.findByLoanId(loan.getId()).stream().findFirst()
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
                .barcode(bookCopy.getBarcode())
                .note(loan.getNote())
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

    @Transactional
    public void deleteLoan(Integer id) {
        // 1. Tìm phiếu mượn tổng
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn với ID: " + id));

        // 2. Tìm TẤT CẢ sách con (chi tiết) nằm trong phiếu này
        List<LoanDetail> details = loanDetailRepository.findByLoanId(id);

        // 3. Kiểm tra ràng buộc: Có sách nào chưa trả không? (Bao trọn cả Đang mượn & Quá hạn)
        for (LoanDetail detail : details) {
            if (detail.getReturnDate() == null) {
                throw new RuntimeException("Phiếu này đang có sách chưa trả (Đang mượn / Quá hạn). Không thể xóa để tránh mất dữ liệu!");
            }
        }

        if (!details.isEmpty()) {
            loanDetailRepository.deleteAll(details); // Xóa con
        }

        loanRepository.delete(loan); // Xóa mẹ
    }

    @Transactional
    public void renewLoanDetail(Integer detailId, RenewLoanRequestDTO request) {
        // 1. Tìm đúng cuốn sách đang mượn
        LoanDetail detail = loanDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết mượn sách!"));

        // 2. Chạy các hàm Ràng buộc (Ví dụ: Đã trả chưa, Có ai đặt trước không...)
        if (detail.getReturnDate() != null) {
            throw new RuntimeException("Sách này đã được trả, không thể gia hạn!");
        }

        if (detail.getDueDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Sách đã quá hạn! Bạn không thể gia hạn. Vui lòng mang sách đến thư viện để nộp phạt.");
        }

        // 3. Gia hạn
        LocalDateTime newDueDate = LocalDate.parse(request.getNewDueDate()).atTime(23, 59, 59);
        if (newDueDate.isBefore(detail.getDueDate())) {
            throw new RuntimeException("Ngày mới phải sau ngày cũ!");
        }

        detail.setDueDate(newDueDate);
        loanDetailRepository.save(detail);
    }

    @Transactional
    public String returnLoanDetail(Integer detailId, ReturnBookRequestDTO request) {
        // 1. Tìm chi tiết mượn
        LoanDetail detail = loanDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết phiếu mượn!"));

        if (detail.getReturnDate() != null) {
            throw new RuntimeException("Cuốn sách này đã được trả trước đó!");
        }

        String borrowedBarcode = detail.getBookCopy().getBarcode(); // Barcode gốc trong DB
        String inputBarcode = (request != null) ? request.getInputBarcode() : null; // Barcode thủ thư nhập

        if (inputBarcode == null || !borrowedBarcode.equalsIgnoreCase(inputBarcode.trim())) {
            throw new RuntimeException("LỖI ĐỐI CHIẾU: Mã vạch không khớp! " +
                    "Mã mong đợi: " + borrowedBarcode + ", Mã nhập vào: " + inputBarcode);
        }

        // 2. Cập nhật phiếu mượn
        detail.setReturnDate(LocalDateTime.now());
        detail.setStatus("returned");

        // 3. Xử lý trạng thái vật lý
        BookCopy copy = detail.getBookCopy();
        String condition = (request != null && request.getConditionStatus() != null)
                ? request.getConditionStatus().toUpperCase() : "GOOD";

        copy.setConditionStatus(condition);

        if (condition.equals("GOOD")) {
            copy.setAvailabilityStatus("AVAILABLE");
            Book book = copy.getBook();
            book.setAvailableQty(book.getAvailableQty() + 1);
            bookRepository.save(book);
        } else {
            // Sách hỏng/mất thì khóa lại, không cộng số lượng
            copy.setAvailabilityStatus("UNAVAILABLE");
        }
        bookCopyRepository.save(copy);

        // ==========================================
        // 4. KIỂM TRA VÀ TỰ ĐỘNG TẠO PHẠT
        // ==========================================
        List<String> fineMessages = new ArrayList<>();
        boolean hasFine = false;

        // --- A. Phạt trễ hạn ---
        if (LocalDateTime.now().isAfter(detail.getDueDate())) {
            long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(
                    detail.getDueDate().toLocalDate(),
                    LocalDate.now()
            );

            if (daysOverdue > 0) {
                Fine fineOverdue = new Fine();
                fineOverdue.setUser(detail.getLoan().getUser());
                fineOverdue.setLoanDetail(detail);
                fineOverdue.setAmount(java.math.BigDecimal.valueOf(daysOverdue * 10000));
                fineOverdue.setReason("Trả trễ hạn " + daysOverdue + " ngày");
                fineOverdue.setIsPaid(false);
                fineRepository.save(fineOverdue);

                hasFine = true;
                fineMessages.add("Trễ hạn (" + (daysOverdue * 10000) + "đ)");
            }
        }

        // --- B. Phạt theo tình trạng (Hư hỏng / Mất) ---
        if (condition.equals("DAMAGED")) {
            Fine fineDamage = new Fine();
            fineDamage.setUser(detail.getLoan().getUser());
            fineDamage.setLoanDetail(detail);
            fineDamage.setAmount(java.math.BigDecimal.valueOf(50000));

            // Lấy thêm ghi chú mà thủ thư gõ vào form (ví dụ: Rách bìa sau)
            String note = (request.getNote() != null && !request.getNote().isEmpty())
                    ? " - " + request.getNote() : "";
            fineDamage.setReason("Sách bị hư hỏng" + note);
            fineDamage.setIsPaid(false);
            fineRepository.save(fineDamage);

            hasFine = true;
            fineMessages.add("Hư hỏng sách (50.000đ)");

        } else if (condition.equals("LOST")) {
            Fine fineLost = new Fine();
            fineLost.setUser(detail.getLoan().getUser());
            fineLost.setLoanDetail(detail);
            fineLost.setAmount(java.math.BigDecimal.valueOf(200000));
            fineLost.setReason("Làm mất sách");
            fineLost.setIsPaid(false);
            fineRepository.save(fineLost);

            hasFine = true;
            fineMessages.add("Mất sách (200.000đ)");
        }

        // Lưu trạng thái hoàn tất
        loanDetailRepository.save(detail);

        // 5. Trả về thông báo động cho Frontend
        if (!hasFine) {
            return "Xác nhận trả sách thành công! Sách nguyên vẹn và đúng hạn.";
        } else {
            return "Trả sách thành công! Hệ thống đã tạo biên lai phạt: "
                    + String.join(", ", fineMessages)
                    + ". Vui lòng qua tab Tiền phạt để thu tiền.";
        }
    }

    @Transactional
    public void createLoanFromReservation(Integer reservationId) {
        // 1. Tìm phiếu đặt
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu đặt với ID: " + reservationId));

        if (!"approved".equalsIgnoreCase(reservation.getStatus())) {
            throw new RuntimeException("Chỉ có thể giao sách cho phiếu đặt đang ở trạng thái Chờ nhận sách!");
        }

        // 2. Kiểm tra độc giả có bị phạt không (Tái sử dụng logic cũ của bạn)
        long unpaidFines = fineRepository.countByUserIdAndIsPaidFalse(reservation.getUser().getId());
        if (unpaidFines > 0) {
            throw new RuntimeException("Độc giả đang có khoản phạt chưa thanh toán. Yêu cầu đóng phạt trước khi nhận sách!");
        }

        BookCopy copyToBorrow = reservation.getBookCopy();
        bookCopyRepository.save(copyToBorrow);

        Book book = copyToBorrow.getBook();
        book.setAvailableQty(book.getAvailableQty() - 1);
        bookRepository.save(book);

        // 4. Tạo hóa đơn mượn (Bảng loans)
        Loan loan = new Loan();
        loan.setUser(reservation.getUser());
        loan.setBorrowDate(LocalDateTime.now()); // Lấy thời gian thực tế lúc giao sách
        loan.setNote("Tạo tự động từ phiếu đặt #RES" + String.format("%03d", reservationId));
        loan = loanRepository.save(loan);

        // 5. Tạo chi tiết mượn (Mặc định cho mượn 14 ngày)
        LoanDetail detail = new LoanDetail();
        detail.setLoan(loan);
        detail.setBookCopy(copyToBorrow);
        detail.setDueDate(LocalDateTime.now().plusDays(14));
        detail.setStatus("borrowing");
        loanDetailRepository.save(detail);

        // 6. CHỐT HẠ: Đổi trạng thái phiếu đặt thành "completed"
        reservation.setStatus("completed");
        reservationRepository.save(reservation);
    }
    public List<Object[]> getRecentLoansForTable() {
        // Chúng ta gọi thẳng xuống Repository để lấy dữ liệu đã join 4 bảng
        return loanDetailRepository.findRecentLoanDetails();
    }
}