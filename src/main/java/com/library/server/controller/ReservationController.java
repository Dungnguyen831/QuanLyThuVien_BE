package com.library.server.controller;

import com.library.server.dto.request.ReservationRequestDTO;
import com.library.server.dto.response.ReservationResponseDTO;
import com.library.server.entity.Book;
import com.library.server.entity.Reservation;
import com.library.server.service.BookService;
import com.library.server.service.ReservationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final BookService bookService;

    public ReservationController(ReservationService reservationService, BookService bookService) {
        this.reservationService = reservationService;
        this.bookService = bookService;
    }

    // POST /api/v1/reservations - Create a new reservation
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequestDTO requestDTO) {
        try {
            ReservationResponseDTO reservation = reservationService.createReservation(requestDTO);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/v1/reservations - Get all reservations
    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
        List<ReservationResponseDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    // GET /api/v1/reservations/{id} - Get reservation by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Integer id) {
        try {
            ReservationResponseDTO reservation = reservationService.getReservationById(id);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET /api/v1/reservations/user/{userId} - Get all reservations for a user with pagination
     * 
     * Query Parameters:
     * - page: Page number (0-indexed), default: 0
     * - size: Page size, default: 10
     * - sort: Sort field with direction (e.g., "reservationDate,desc"), default: "createdAt,desc"
     * 
     * Examples:
     * - GET /api/v1/reservations/user/1?page=0&size=10
     * - GET /api/v1/reservations/user/1?page=0&size=5&sort=reservationDate,desc
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReservationsByUser(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        try {
            // Validate inputs
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().body("User ID không hợp lệ");
            }
            if (page < 0) {
                return ResponseEntity.badRequest().body("Trang phải >= 0");
            }
            if (size <= 0) {
                return ResponseEntity.badRequest().body("Size phải > 0");
            }

            // Parse sort parameter
            String[] sortParams = sort.split(",");
            String sortBy = sortParams[0];
            Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;

            // Create pageable
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            // Get reservations
            Page<ReservationResponseDTO> reservations = reservationService.getReservationsByUserId(userId, pageable);

            // Return empty page if no reservations found
            if (reservations.isEmpty()) {
                return ResponseEntity.ok().body(new EmptyPageResponse(userId));
            }

            return ResponseEntity.ok(reservations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET /api/v1/reservations/user/{userId} - Get user's reservations with FULL book details
     * 
     * ✅ Returns custom object kết hợp Reservation + Book information
     * ✅ Frontend có thể lấy: reservationDate, status, book title, author, cover...
     * 
     * Example:
     * - GET /api/v1/reservations/user/1
     */
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<?> getUserReservationsWithBooks(@PathVariable Integer userId) {
        try {
            // Validate user ID
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().body("User ID không hợp lệ");
            }

            // Get user's reservations (without pagination)
            List<ReservationResponseDTO> reservations = reservationService.getReservationsByUserIdList(userId);

            // ✅ Map sang object kết hợp Reservation + Book info
            List<Object> result = reservations.stream()
                .map(reservation -> {
                    Book book = bookService.getBookById(reservation.getBookId());
                    
                    // Return Map object kết hợp thông tin
                    return new java.util.HashMap<String, Object>() {{
                        put("id", reservation.getId());
                        put("reservationDate", reservation.getReservationDate());
                        put("status", reservation.getStatus());
                        put("createdAt", reservation.getCreatedAt());
                        put("updatedAt", reservation.getUpdatedAt());
                        
                        // Book details
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
                    }};
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/v1/reservations/{id} - Update reservation
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable Integer id,
                                               @RequestBody ReservationRequestDTO requestDTO) {
        try {
            ReservationResponseDTO reservation = reservationService.updateReservation(id, requestDTO);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/v1/reservations/{id} - Delete reservation
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Integer id) {
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.ok("Đặt chỗ đã được xóa thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Inner class for empty page response
     */
    public static class EmptyPageResponse {
        public final Integer userId;
        public final String message;
        public final int totalElements;
        public final int totalPages;

        public EmptyPageResponse(Integer userId) {
            this.userId = userId;
            this.message = "Không tìm thấy đặt chỗ nào cho người dùng này";
            this.totalElements = 0;
            this.totalPages = 0;
        }
    }
}

