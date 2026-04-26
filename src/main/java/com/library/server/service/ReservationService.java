package com.library.server.service;

import com.library.server.dto.request.ReservationRequestDTO;
import com.library.server.dto.response.ReservationResponseDTO;
import com.library.server.entity.Reservation;
import com.library.server.entity.Book;
import com.library.server.entity.User;
import com.library.server.repository.ReservationRepository;
import com.library.server.repository.BookRepository;
import com.library.server.repository.UserRepository;
import com.library.server.repository.LoanDetailRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanDetailRepository loanDetailRepository;


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

        List<String> activeStatuses = List.of("pending", "approved");
        long activeReservationCount = reservationRepository.countByUserIdAndStatusIn(authenticatedUserId, activeStatuses);
        if (activeReservationCount >= 3) {
            throw new IllegalStateException("Bạn đã đạt giới hạn 3 đặt chỗ đang hoạt động.");
        }

        if (book.getAvailableQty() == null || book.getAvailableQty() <= 0) {
            throw new IllegalStateException("Sách hiện không còn trong kho.");
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .book(book)
                .reservationDate(requestDTO.getReservationDate())
                .status(requestDTO.getStatus())
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
        reservation.setReservationDate(requestDTO.getReservationDate());
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
            if (book.getAvailableQty() <= 0) {
                throw new RuntimeException("Sách này hiện đã hết trong kho, không thể duyệt!");
            }
            book.setAvailableQty(book.getAvailableQty() - 1);
            bookRepository.save(book);
        } else if ("approved".equalsIgnoreCase(oldStatus) && "cancelled".equalsIgnoreCase(newStatus)) {
            Book book = reservation.getBook();
            book.setAvailableQty(book.getAvailableQty() + 1);
            bookRepository.save(book);
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

    /**
     * ✅ NEW: Get all reservations with full book details (Admin only)
     * Used by: GET /api/v1/reservations
     */
//    public List<java.util.Map<String, Object>> getAllReservationsWithBooksDetail() {
//        List<ReservationResponseDTO> reservations = getAllReservations();
//        return mapReservationsWithBooks(reservations);
//    }

    /**
     * ✅ NEW: Get single reservation with full book details (Admin only)
     * Used by: GET /api/v1/reservations/{id}
     */
    public java.util.Map<String, Object> getReservationWithBooksDetail(Integer reservationId) {
        ReservationResponseDTO reservation = getReservationByIdForAdmin(reservationId);
        Book book = bookRepository.findById(reservation.getBookId()).orElse(null);
        return buildReservationMap(reservation, book, true);
    }

    /**
     * ✅ HELPER: Map reservations to HashMap with book details
     */
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
}
