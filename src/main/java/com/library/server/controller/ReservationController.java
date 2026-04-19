package com.library.server.controller;

import com.library.server.dto.request.ReservationRequestDTO;
import com.library.server.dto.response.ReservationResponseDTO;
import com.library.server.entity.Book;
import com.library.server.entity.Reservation;
import com.library.server.entity.User;
import com.library.server.service.BookService;
import com.library.server.service.ReservationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;
    private final BookService bookService;

    public ReservationController(ReservationService reservationService, BookService bookService) {
        this.reservationService = reservationService;
        this.bookService = bookService;
    }

    // POST /api/v1/reservations - Create a new reservation
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createReservation(
            @RequestBody @Valid ReservationRequestDTO requestDTO,
            @AuthenticationPrincipal User authenticatedUser) {  // ✅ Extract from JWT
        try {
            logger.info("User {} creating reservation for bookId: {}", 
                authenticatedUser.getId(), requestDTO.getBookId());
            // ✅ SECURITY: Pass authenticatedUser.getId() instead of requestDTO.getUserId()
            ReservationResponseDTO reservation = reservationService.createReservation(authenticatedUser.getId(), requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid reservation request from user {}: {}", authenticatedUser.getId(), e.getMessage());
            return ResponseEntity.badRequest().body(
                new ErrorResponseBody("Yêu cầu không hợp lệ. Vui lòng kiểm tra dữ liệu.", 400)
            );
        } catch (Exception e) {
            logger.error("Error creating reservation for user: " + authenticatedUser.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseBody("Lỗi máy chủ. Vui lòng thử lại sau.", 500)
            );
        }
    }


    // GET /api/v1/reservations/user/{id} - Get user's specific reservation by ID
    @GetMapping("/user/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getReservationByUserId(
            @PathVariable Integer id,
            @AuthenticationPrincipal User authenticatedUser) {  // ✅ Extract from JWT
        try {
            if (id == null || id <= 0) {
                logger.warn("Invalid reservation ID: {} for user: {}", id, authenticatedUser.getId());
                return ResponseEntity.badRequest().body(
                    new ErrorResponseBody("ID không hợp lệ", 400)
                );
            }
            // ✅ SECURITY: Pass authenticatedUser.getId() for ownership verification
            ReservationResponseDTO reservation = reservationService.getReservationById(id, authenticatedUser.getId());
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException e) {
            logger.warn("Reservation access denied for user {}: {}", authenticatedUser.getId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseBody("Đặt chỗ không tồn tại", 404)
            );
        } catch (Exception e) {
            logger.error("Error retrieving reservation {} for user: {}", id, authenticatedUser.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseBody("Lỗi máy chủ. Vui lòng thử lại sau.", 500)
            );
        }
    }


    /**
     * ✅ FIXED: GET /api/v1/reservations/details - Get authenticated user's reservations with FULL book details
     * 
     * Gets user ID from JWT token (SecurityContext), NOT from URL parameter
     * Returns reservation data combined with book information
     */
    @GetMapping("/details")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyReservationsWithBooks(
            @AuthenticationPrincipal User authenticatedUser) {  // ✅ Get user from JWT token
        try {
            logger.info("User {} fetching reservations with book details", authenticatedUser.getId());

            // ✅ Uses authenticated user's ID from JWT, not from request
            List<ReservationResponseDTO> reservations = 
                reservationService.getReservationsByUserIdList(authenticatedUser.getId());

            // Map to object combining Reservation + Book info
            List<Object> result = reservations.stream()
                .map(reservation -> {
                    try {
                        Book book = bookService.getBookById(reservation.getBookId());
                        
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
                        logger.warn("Error fetching book details for reservation: {} by user: {}", 
                            reservation.getId(), authenticatedUser.getId(), e);
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

            logger.info("Retrieved {} reservations with book details for user: {}", result.size(), authenticatedUser.getId());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request from user {}: {}", authenticatedUser.getId(), e.getMessage());
            return ResponseEntity.badRequest().body(
                new ErrorResponseBody(e.getMessage(), 400)
            );
        } catch (Exception e) {
            logger.error("Error retrieving reservations with book details for user: " + authenticatedUser.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseBody("Lỗi máy chủ. Vui lòng thử lại sau.", 500)
            );
        }
    }

    // PUT /api/v1/reservations/{id} - Update reservation
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateReservation(
            @PathVariable Integer id,
            @RequestBody @Valid ReservationRequestDTO requestDTO,
            @AuthenticationPrincipal User authenticatedUser) {  // ✅ Extract from JWT
        try {
            if (id == null || id <= 0) {
                logger.warn("Invalid reservation ID for update: {} for user: {}", id, authenticatedUser.getId());
                return ResponseEntity.badRequest().body(
                    new ErrorResponseBody("ID không hợp lệ", 400)
                );
            }
            logger.info("User {} updating reservation ID: {}", authenticatedUser.getId(), id);
            logger.debug("Update request - bookId: {}, status: {}, date: {}",
                requestDTO.getBookId(), requestDTO.getStatus(), requestDTO.getReservationDate());

            // ✅ SECURITY: Pass authenticatedUser.getId() for ownership verification
            ReservationResponseDTO reservation = reservationService.updateReservation(id, authenticatedUser.getId(), requestDTO);

            logger.info("Successfully updated reservation {} for user {}", id, authenticatedUser.getId());
            return ResponseEntity.ok(reservation);

        } catch (IllegalArgumentException e) {
            logger.warn("Update failed for user {}: {}", authenticatedUser.getId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseBody("Đặt chỗ không tồn tại", 404)
            );
        } catch (Exception e) {
            logger.error("Error updating reservation {} for user: {} - Exception: {}", id, authenticatedUser.getId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseBody("Lỗi máy chủ: " + e.getMessage(), 500)
            );
        }
    }

    /**
     * ✅ ADMIN ONLY: GET /api/v1/reservations/{id} - Get reservation by ID (for admin management)
     * 
     * Gets a specific reservation by ID without ownership check
     * Only accessible to ADMIN users
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getReservationForAdmin(
            @PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                logger.warn("Invalid reservation ID: {}", id);
                return ResponseEntity.badRequest().body(
                    new ErrorResponseBody("ID không hợp lệ", 400)
                );
            }

            logger.info("Admin fetching reservation ID: {}", id);

            // ✅ Get reservation by ID without ownership check (admin can view any reservation)
            ReservationResponseDTO reservation = reservationService.getReservationByIdForAdmin(id);

            // Get book details
            try {
                Book book = bookService.getBookById(reservation.getBookId());
                
                java.util.HashMap<String, Object> result = new java.util.HashMap<String, Object>() {{
                    put("id", reservation.getId());
                    put("userId", reservation.getUserId());
                    put("reservationDate", reservation.getReservationDate());
                    put("status", reservation.getStatus());
                    put("createdAt", reservation.getCreatedAt());
                    put("updatedAt", reservation.getUpdatedAt());
                    
                    // Book details
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

                logger.info("Admin successfully retrieved reservation {}", id);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                logger.warn("Error fetching book details for reservation: {}", id, e);
                // Return reservation without book details
                return ResponseEntity.ok(reservation);
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Reservation not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseBody("Đặt chỗ không tồn tại", 404)
            );
        } catch (Exception e) {
            logger.error("Error retrieving reservation {} for admin", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseBody("Lỗi máy chủ. Vui lòng thử lại sau.", 500)
            );
        }
    }

    // DELETE /api/v1/reservations/{id} - Delete reservation
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteReservation(
            @PathVariable Integer id,
            @AuthenticationPrincipal User authenticatedUser) {  // ✅ Extract from JWT
        try {
            if (id == null || id <= 0) {
                logger.warn("Invalid reservation ID for deletion: {} for user: {}", id, authenticatedUser.getId());
                return ResponseEntity.badRequest().body(
                    new ErrorResponseBody("ID không hợp lệ", 400)
                );
            }
            logger.info("User {} deleting reservation ID: {}", authenticatedUser.getId(), id);
            // ✅ SECURITY: Pass authenticatedUser.getId() for ownership verification
            reservationService.deleteReservation(id, authenticatedUser.getId());
            return ResponseEntity.ok("Đặt chỗ đã được xóa thành công");
        } catch (IllegalArgumentException e) {
            logger.warn("Delete failed for user {}: {}", authenticatedUser.getId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseBody("Đặt chỗ không tồn tại", 404)
            );
        } catch (Exception e) {
            logger.error("Error deleting reservation {} for user: {}", id, authenticatedUser.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseBody("Lỗi máy chủ. Vui lòng thử lại sau.", 500)
            );
        }
    }

    /**
     * ✅ ADMIN ONLY: GET /api/v1/reservations - Get all reservations (for admin management)
     * 
     * Returns all reservations in the system with book details
     * Only accessible to ADMIN users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllReservations() {
        try {
            logger.info("Admin fetching all reservations for management");

            // Get all reservations from service
            List<ReservationResponseDTO> reservations = 
                reservationService.getAllReservations();

            // Map to object combining Reservation + Book info
            List<Object> result = reservations.stream()
                .map(reservation -> {
                    try {
                        Book book = bookService.getBookById(reservation.getBookId());
                        
                        return new java.util.HashMap<String, Object>() {{
                            put("id", reservation.getId());
                            put("userId", reservation.getUserId());
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
                            put("userId", reservation.getUserId());
                            put("reservationDate", reservation.getReservationDate());
                            put("status", reservation.getStatus());
                            put("createdAt", reservation.getCreatedAt());
                            put("updatedAt", reservation.getUpdatedAt());
                            put("bookId", reservation.getBookId());
                        }};
                    }
                })
                .collect(Collectors.toList());

            logger.info("Retrieved {} total reservations for admin management", result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error retrieving all reservations for admin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseBody("Lỗi máy chủ. Vui lòng thử lại sau.", 500)
            );
        }
    }
    
    /**
     * Inner class for error response
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

