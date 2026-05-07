package com.library.server.service;

import com.library.server.dto.request.ReservationRequestDTO;
import com.library.server.dto.response.ReservationResponseDTO;
import com.library.server.entity.Reservation;
import com.library.server.entity.Book;
import com.library.server.entity.User;
import com.library.server.entity.Loan;
import com.library.server.entity.LoanDetail;
import com.library.server.entity.BookCopy;
import com.library.server.repository.ReservationRepository;
import com.library.server.repository.BookRepository;
import com.library.server.repository.UserRepository;
import com.library.server.repository.LoanDetailRepository;
import com.library.server.repository.LoanRepository;
import com.library.server.repository.BookCopyRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanDetailRepository loanDetailRepository;
    private final LoanRepository loanRepository;
    private final BookCopyRepository bookCopyRepository;


    // Helper method: Convert Reservation Entity to ReservationResponseDTO
    private ReservationResponseDTO mapToDTO(Reservation reservation) {
        // Xử lý an toàn null cho Tên và Email
        String finalUserName = "Không xác định";
        String finalUserEmail = "";
        if (reservation.getUser() != null) {
            finalUserName = reservation.getUser().getFullName();
            finalUserEmail = reservation.getUser().getEmail();
        }

        // Xử lý an toàn null cho Tên sách
        String finalBookName = "Sách không tồn tại / Đã xóa";
        if (reservation.getBook() != null) {
            finalBookName = reservation.getBook().getTitle(); // Lấy title gắn vào bookName
        }

        // Trả về đúng DTO gọn gàng của bạn
        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .userId(reservation.getUser() != null ? reservation.getUser().getId() : null)
                .bookId(reservation.getBook() != null ? reservation.getBook().getId() : null)
                .bookCopyBarcode(reservation.getBookCopy() != null ? reservation.getBookCopy().getBarcode() : null)
                .userName(finalUserName)
                .userEmail(finalUserEmail)
                .bookName(finalBookName)
                .reservationDate(reservation.getReservationDate())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }

    // Create a new reservation
    @Transactional
    public ReservationResponseDTO createReservation(Integer authenticatedUserId, ReservationRequestDTO requestDTO) {
        if (authenticatedUserId == null || authenticatedUserId <= 0) {
            throw new IllegalArgumentException("User ID không hợp lệ");
        }

        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + requestDTO.getBookId()));

        if (reservationRepository.existsByUserIdAndBookIdAndStatus(authenticatedUserId, requestDTO.getBookId(), "pending")) {
            throw new IllegalStateException("Bạn đã đặt sách này rồi. Vui lòng chờ xử lý.");
        }

        long pendingReservations = reservationRepository.countByUserIdAndStatusIn(
                authenticatedUserId,
                List.of("pending")
        );
        long borrowingLoans = countBorrowingLoans(authenticatedUserId);
        if (pendingReservations + borrowingLoans >= 5) {
            throw new IllegalStateException("Bạn chỉ được tối đa 5 sách đang mượn/đang chờ duyệt.");
        }

        BookCopy assignedCopy = null;
        List<BookCopy> availableCopies = bookCopyRepository.findByBookIdAndAvailabilityStatus(book.getId(), "AVAILABLE");
        if (availableCopies.isEmpty()) {
            availableCopies = bookCopyRepository.findByBookIdAndAvailabilityStatus(book.getId(), "available");
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .book(book)
                .bookCopy(assignedCopy)
                .reservationDate(null)
                .status("pending")
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        return mapToDTO(savedReservation);
    }

    // Get all reservations
    // ✅ REMOVED: This method is not exposed in Controller endpoint
    // FE uses getReservationsByUserId() with pagination or getReservationsByUserIdList() instead

    // Get reservation by ID
    public ReservationResponseDTO getReservationById(Integer id, Integer authenticatedUserId) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ");
        }
        
        if (authenticatedUserId == null || authenticatedUserId <= 0) {
            throw new IllegalArgumentException("User ID không hợp lệ");
        }
        
        Reservation reservation = reservationRepository.findByIdAndUserId(id, authenticatedUserId)
                .orElseThrow(() -> new IllegalArgumentException("Đặt chỗ không tồn tại"));

        return mapToDTO(reservation);
    }

    // Update reservation
    public ReservationResponseDTO updateReservation(Integer id, Integer authenticatedUserId, ReservationRequestDTO requestDTO) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ");
        }
        
        if (authenticatedUserId == null || authenticatedUserId <= 0) {
            throw new IllegalArgumentException("User ID không hợp lệ");
        }

        Reservation reservation = reservationRepository.findByIdAndUserId(id, authenticatedUserId)
                .orElseThrow(() -> new IllegalArgumentException("Đặt chỗ không tồn tại"));

        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại"));

        reservation.setUser(user);
        reservation.setBook(book);
        // reservationDate is set by backend logic on approval
        reservation.setStatus(requestDTO.getStatus());

        Reservation updatedReservation = reservationRepository.save(reservation);
        return mapToDTO(updatedReservation);
    }

    // Delete reservation
    public void deleteReservation(Integer id, Integer authenticatedUserId) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ");
        }

        if (authenticatedUserId == null || authenticatedUserId <= 0) {
            throw new IllegalArgumentException("User ID không hợp lệ");
        }

        Reservation reservation = reservationRepository.findByIdAndUserId(id, authenticatedUserId)
                .orElseThrow(() -> new IllegalArgumentException("Đặt chỗ không tồn tại"));

        BookCopy bookStatus = reservation.getBookCopy();
        bookStatus.setAvailabilityStatus("AVAILABLE");
        reservationRepository.delete(reservation);



    }


    /**
     * Get all reservations for a specific user without pagination
     * ✅ SECURITY: Validates that user exists before querying
     * @param userId User ID (must be from authenticated user - controller already validates)
     * @return List of ReservationResponseDTO
     */
    public List<ReservationResponseDTO> getReservationsByUserIdList(Integer userId) {
        // Validate user ID format
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID không hợp lệ");
        }
        
        // Verify user exists (security check - should only reach here if user is from JWT token)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        // Get reservations - Repository handles WHERE user_id = ?
        List<Reservation> reservations = reservationRepository.findByUserId(userId);

        // Handle case when no reservations found
        if (reservations.isEmpty()) {
            return List.of();
        }

        // Convert to DTO
        return reservations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * ✅ ADMIN ONLY: Get reservation by ID without ownership check
     * Used by admin to view any reservation in the system
     * @param id Reservation ID
     * @return ReservationResponseDTO
     */
    public ReservationResponseDTO getReservationByIdForAdmin(Integer id) {
        // Validate ID
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ");
        }

        // Get reservation without ownership check
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đặt chỗ không tồn tại"));

        return mapToDTO(reservation);
    }

    /**
     * ✅ ADMIN ONLY: Get all reservations in the system
     * Used for admin management dashboard
     * @return List of all ReservationResponseDTO
     */
    public List<ReservationResponseDTO> getAllReservations() {
        // Get all reservations from database
        List<Reservation> allReservations = reservationRepository.findAll();

        // Handle case when no reservations found
        if (allReservations.isEmpty()) {
            return List.of();
        }

        // Convert to DTO
        return allReservations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void updateStatus(Integer id, String newStatus) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu đặt với ID: " + id));

        String oldStatus = reservation.getStatus();

        if ("pending".equalsIgnoreCase(oldStatus) && "approved".equalsIgnoreCase(newStatus)) {
            Integer userId = reservation.getUser() != null ? reservation.getUser().getId() : null;
            Integer bookId = reservation.getBook() != null ? reservation.getBook().getId() : null;

            if (userId != null && bookId != null) {
                List<String> activeStatuses = List.of("pending", "approved");
                boolean hasOtherReservation = reservationRepository.findByUserId(userId).stream()
                        .anyMatch(item -> !item.getId().equals(reservation.getId())
                                && item.getBook() != null
                                && bookId.equals(item.getBook().getId())
                                && item.getStatus() != null
                                && activeStatuses.contains(item.getStatus().toLowerCase()));

                if (hasOtherReservation) {
                    throw new RuntimeException("Độc giả đã có phiếu đặt khác cho sách này.");
                }

                boolean hasBorrowingLoan = loanDetailRepository
                        .existsBorrowingByUserAndBook(userId, bookId, "borrowing");

                if (hasBorrowingLoan) {
                    throw new RuntimeException("Độc giả đang mượn sách này, không thể duyệt đặt chỗ.");
                }
            }

            Book book = reservation.getBook();

            List<BookCopy> availableCopies = bookCopyRepository.findByBookIdAndAvailabilityStatus(bookId, "AVAILABLE");
            if (availableCopies.isEmpty()) {
                throw new RuntimeException("Đầu sách này hiện không còn cuốn nào trong kho!");
            }
            BookCopy copyToBorrow = availableCopies.get(0); // Lấy cuốn đầu tiên tìm thấy
            copyToBorrow.setAvailabilityStatus("UNAVAILABLE");

//            if (copyToBorrow == null) {
//                List<BookCopy> reservedCopies = bookCopyRepository.findByBookIdAndAvailabilityStatus(book.getId(), "RESERVED");
//                if (reservedCopies.isEmpty()) {
//                    reservedCopies = bookCopyRepository.findByBookIdAndAvailabilityStatus(book.getId(), "reserved");
//                }
//
//                if (!reservedCopies.isEmpty()) {
//                    copyToBorrow = reservedCopies.get(0);
//                } else {
//                    List<BookCopy> availableCopies = bookCopyRepository.findByBookIdAndAvailabilityStatus(book.getId(), "AVAILABLE");
//                    if (availableCopies.isEmpty()) {
//                        availableCopies = bookCopyRepository.findByBookIdAndAvailabilityStatus(book.getId(), "available");
//                    }
//                    if (!availableCopies.isEmpty()) {
//                        copyToBorrow = availableCopies.get(0);
//                    }
//                }
//            }

            if (copyToBorrow == null) {
                throw new RuntimeException("Sách này hiện đã hết trong kho, không thể duyệt!");
            }

            copyToBorrow.setAvailabilityStatus("BORROWED");
            bookCopyRepository.save(copyToBorrow);
            reservation.setBookCopy(copyToBorrow);

            book.setAvailableQty(book.getAvailableQty() - 1);
            bookRepository.save(book);

            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime now = ZonedDateTime.now(zoneId);
            LocalDate dateToUse = now.toLocalDate();
            if (!now.toLocalTime().isBefore(LocalTime.of(15, 0))) {
                dateToUse = dateToUse.plusDays(1);
            }
            reservation.setReservationDate(LocalDateTime.of(dateToUse, LocalTime.MIDNIGHT));
        } else if ("approved".equalsIgnoreCase(oldStatus) && "cancelled".equalsIgnoreCase(newStatus)) {
            Book book = reservation.getBook();
            book.setAvailableQty(book.getAvailableQty() + 1);
            bookRepository.save(book);

            BookCopy copyToRelease = reservation.getBookCopy();
            if (copyToRelease != null) {
                copyToRelease.setAvailabilityStatus("AVAILABLE");
                bookCopyRepository.save(copyToRelease);
            }
        }

        reservation.setStatus(newStatus);
        reservationRepository.save(reservation);
    }

    /**
     * ✅ NEW: Get user's reservations with full book details
     * Used by: GET /api/v1/reservations/details
     */
    public List<java.util.Map<String, Object>> getMyReservationsWithBooksDetail(Integer userId) {
        List<ReservationResponseDTO> reservations = getReservationsByUserIdList(userId);
        return mapReservationsWithBooks(reservations);
    }


    public java.util.Map<String, Object> getReservationWithBooksDetail(Integer reservationId) {
        ReservationResponseDTO reservation = getReservationByIdForAdmin(reservationId);
        Book book = bookRepository.findById(reservation.getBookId()).orElse(null);
        return buildReservationMap(reservation, book, true);
    }

    private List<java.util.Map<String, Object>> mapReservationsWithBooks(List<ReservationResponseDTO> reservations) {
        return reservations.stream()
                .map(reservation -> {
                    try {
                        Book book = bookRepository.findById(reservation.getBookId()).orElse(null);
                        return buildReservationMap(reservation, book, false);
                    } catch (Exception e) {
                        return buildReservationMap(reservation, null, false);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * ✅ HELPER: Build reservation map with optional book details
     */
    private java.util.Map<String, Object> buildReservationMap(ReservationResponseDTO reservation, Book book, boolean includeUserId) {
        return new java.util.HashMap<String, Object>() {{
            put("id", reservation.getId());
            if (includeUserId) {
                put("userId", reservation.getUserId());
            }
            put("reservationDate", reservation.getReservationDate());
            put("status", reservation.getStatus());
            put("createdAt", reservation.getCreatedAt());
            put("updatedAt", reservation.getUpdatedAt());
            put("bookCopyBarcode", reservation.getBookCopyBarcode());

            // Book details (null-safe)
            if (book != null) {
                put("bookId", book.getId());
                put("title", book.getTitle());
                put("isbn", book.getIsbn());
                put("publishedYear", book.getPublishedYear());
                put("totalQty", book.getTotalQty());
                put("availableQty", book.getAvailableQty());
                put("imageUrl", book.getImageUrl());
                put("categoryId", book.getCategory() != null ? book.getCategory().getId() : null);
                put("authorId", book.getAuthor() != null ? book.getAuthor().getId() : null);
                put("publisherId", book.getPublisher() != null ? book.getPublisher().getId() : null);
            }
        }};
    }

    private long countBorrowingLoans(Integer userId) {
        List<Loan> loans = loanRepository.findByUserId(userId);
        if (loans.isEmpty()) {
            return 0;
        }

        long borrowingCount = 0;
        for (Loan loan : loans) {
            List<LoanDetail> details = loanDetailRepository.findByLoanId(loan.getId());
            for (LoanDetail detail : details) {
                if (detail.getStatus() != null && "borrowing".equalsIgnoreCase(detail.getStatus())) {
                    borrowingCount++;
                }
            }
        }

        return borrowingCount;
    }
}

