package com.library.server.controller;

import com.library.server.dto.request.ReservationRequestDTO;
import com.library.server.dto.response.ReservationResponseDTO;
import com.library.server.entity.Book;
import com.library.server.entity.Reservation;
import com.library.server.service.BookService;
import com.library.server.service.ReservationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    
    // ✅ SECURITY FIX: Whitelist allowed sort fields (prevents column injection)
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
        "id", "reservationDate", "status", "createdAt", "updatedAt"
    );

    private final ReservationService reservationService;
    private final BookService bookService;

    public ReservationController(ReservationService reservationService, BookService bookService) {
        this.reservationService = reservationService;
        this.bookService = bookService;
    }

    // POST /api/v1/reservations - Create a new reservation
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody @Valid ReservationRequestDTO requestDTO) {
        try {
            logger.info("Creating reservation for userId: {}, bookId: {}", 
                requestDTO.getUserId(), requestDTO.getBookId());
            ReservationResponseDTO reservation = reservationService.createReservation(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid reservation request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                new ErrorResponseBody("Yêu cầu không hợp lệ. Vui lòng kiểm tra dữ liệu.", 400)
            );
        } catch (Exception e) {
            logger.error("Error creating reservation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseBody("Lỗi máy chủ. Vui lòng thử lại sau.", 500)
            );
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
            if (id == null || id <= 0) {
                logger.warn("Invalid reservation ID: {}", id);
                return ResponseEntity.badRequest().body(
                    new ErrorResponseBody("ID không hợp lệ", 400)
                );
            }
            ReservationResponseDTO reservation = reservationService.getReservationById(id);
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException e) {
            logger.warn("Reservation not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseBody("Đặt chỗ không tồn tại", 404)
            );
        } catch (Exception e) {
            logger.error("Error retrieving reservation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseBody("Lỗi máy chủ. Vui lòng thử lại sau.", 500)
            );
        }
    }

    /**
     * GET /api/v1/reservations/user/{userId} - Get all reservations for a user with pagination
     * 
     * Query Parameters:
     * - page: Page number (0-indexed), default: 0
     * - size: Page size (1-100), default: 10
     * - sort: Sort field with direction (e.g., "reservationDate,desc"), default: "createdAt,desc"
     * 
     * ✅ SECURITY: Size limit prevents DoS, sort field whitelist prevents column injection
     * 
     * Examples:
     * - GET /api/v1/reservations/user/1?page=0&size=10
     * - GET /api/v1/reservations/user/1?page=0&size=5&sort=reservationDate,desc
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReservationsByUser(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") 
            @Min(value = 1, message = "Size tối thiểu là 1")
            @Max(value = 100, message = "Size tối đa là 100")
            int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        try {
            // Validate user ID
            if (userId == null || userId <= 0) {
                logger.warn("Invalid userId: {}", userId);
                return ResponseEntity.badRequest().body(
                    new ErrorResponseBody("User ID không hợp lệ", 400)
                );
            }
            
            // Validate page
            if (page < 0) {
                logger.warn("Invalid page number: {}", page);
                return ResponseEntity.badRequest().body(
                    new ErrorResponseBody("Trang phải >= 0", 400)
                );
            }

            // ✅ SECURITY FIX: Validate sort field against whitelist (prevents column injection)
            String[] sortParams = sort.split(",");
            String sortBy = sortParams[0].trim();
            
            if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
                logger.warn("Invalid sort field attempted: {}", sortBy);
                throw new IllegalArgumentException(
                    "Sort field '" + sortBy + "' không hợp lệ. Các trường cho phép: " + 
                    String.join(", ", ALLOWED_SORT_FIELDS)
                );
            }

            // Parse sort direction
            Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;

            // Create pageable with validated sort
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            // Get reservations
            Page<ReservationResponseDTO> reservations = reservationService.getReservationsByUserId(userId, pageable);

            // Return empty page if no reservations found
            if (reservations.isEmpty()) {
                logger.info("No reservations found for userId: {}", userId);
                return ResponseEntity.ok().body(new EmptyPageResponse(userId));
            }

            logger.info("Retrieved {} reservations for userId: {}", reservations.getTotalElements(), userId);
            return ResponseEntity.ok(reservations);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                new ErrorResponseBody(e.getMessage(), 400)
            );
        } catch (Exception e) {
            logger.error("Error retrieving reservations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseBody("Lỗi máy chủ. Vui lòng thử lại sau.", 500)
            );
        }
    }

    /**
     * GET /api/v1/reservations/user/{userId}/all - Get user's reservations with FULL book details
     * 
     * ✅ Returns custom object kết hợp Reservation + Book information
     * ✅ Frontend có thể lấy: reservationDate, status, book title, author, cover...
     * 
     * Example:
     * - GET /api/v1/reservations/user/1/all
     */
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<?> getUserReservationsWithBooks(@PathVariable Integer userId) {
        try {
            // Validate user ID
            if (userId == null || userId <= 0) {
                logger.warn("Invalid userId: {}", userId);
                return ResponseEntity.badRequest().body(
                    new ErrorResponseBody("User ID không hợp lệ", 400)
                );
            }

            logger.info("Fetching all reservations with book details for userId: {}", userId);

            // Get user's reservations (without pagination)
            List<ReservationResponseDTO> reservations = reservationService.getReservationsByUserIdList(userId);

            // ✅ Map sang object kết hợp Reservation + Book info
            List<Object> result = reservations.stream()
                .map(reservation -> {
                    try {
                        Book book = bookService.getBookById(reservation.getBookId());
                        
                        // Return Map object kết hợp thông tin
                        return new java.util.HashMap<String, Object>() {{
                            put("id", reservation.getId());
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
                    } catch (Exception e) {
                        logger.warn("Error fetching book details for reservation: {}", reservation.getId(), e);
                        // Return reservation without book details if error occurs
                        return new java.util.HashMap<String, Object>() {{
                            put("id", reservation.getId());
                            put("reservationDate", reservation.getReservationDate());
                            put("status", reservation.getStatus());
                            put("createdAt", reservation.getCreatedAt());
                            put("updatedAt", reservation.getUpdatedAt());
                            put("bookId", reservation.getBookId());
                        }};
                    }
                })
                .collect(Collectors.toList());

            logger.info("Retrieved {} reservations with book details for userId: {}", result.size(), userId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                new ErrorResponseBody(e.getMessage(), 400)
            );
        } catch (Exception e) {
            logger.error("Error retrieving reservations with book details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseBody("Lỗi máy chủ. Vui lòng thử lại sau.", 500)
            );
        }
    }

    // PUT /api/v1/reservations/{id} - Update reservation
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable Integer id,
                                               @RequestBody @Valid ReservationRequestDTO requestDTO) {
        try {
            if (id == null || id <= 0) {
                logger.warn("Invalid reservation ID for update: {}", id);
                return ResponseEntity.badRequest().body(
                    new ErrorResponseBody("ID không hợp lệ", 400)
                );
            }
            logger.info("Updating reservation ID: {}", id);
            ReservationResponseDTO reservation = reservationService.updateReservation(id, requestDTO);
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException e) {
            logger.warn("Update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseBody("Đặt chỗ không tồn tại", 404)
            );
        } catch (Exception e) {
            logger.error("Error updating reservation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseBody("Lỗi máy chủ. Vui lòng thử lại sau.", 500)
            );
        }
    }

    // DELETE /api/v1/reservations/{id} - Delete reservation
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                logger.warn("Invalid reservation ID for deletion: {}", id);
                return ResponseEntity.badRequest().body(
                    new ErrorResponseBody("ID không hợp lệ", 400)
                );
            }
            logger.info("Deleting reservation ID: {}", id);
            reservationService.deleteReservation(id);
            return ResponseEntity.ok("Đặt chỗ đã được xóa thành công");
        } catch (IllegalArgumentException e) {
            logger.warn("Delete failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseBody("Đặt chỗ không tồn tại", 404)
            );
        } catch (Exception e) {
            logger.error("Error deleting reservation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseBody("Lỗi máy chủ. Vui lòng thử lại sau.", 500)
            );
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

    /**
     * Secure error response - never exposes sensitive information
     */
    public static class ErrorResponseBody {
        public final int code;
        public final String message;
        public final LocalDateTime timestamp;

        public ErrorResponseBody(String message, int code) {
            this.code = code;
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }
    }
}

