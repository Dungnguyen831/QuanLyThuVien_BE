package com.library.server.controller;

import com.library.server.dto.request.ReservationRequestDTO;
import com.library.server.dto.response.ReservationResponseDTO;
import com.library.server.entity.User;
import com.library.server.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // POST /api/v1/reservations - Create a new reservation
    @PostMapping
    public ResponseEntity<?> createReservation(
            @RequestBody @Valid ReservationRequestDTO requestDTO,
            @AuthenticationPrincipal User authenticatedUser) {
        try {
            ReservationResponseDTO reservation = reservationService.createReservation(authenticatedUser.getId(), requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                new ErrorResponseBody("Yêu cầu không hợp lệ. Vui lòng kiểm tra dữ liệu.", 400)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseBody("Lỗi máy chủ. Vui lòng thử lại sau.", 500)
            );
        }
    }


    // GET /api/v1/reservations/user/{id} - Get user's specific reservation by ID
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getReservationByUserId(
            @PathVariable Integer id,
            @AuthenticationPrincipal User authenticatedUser) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponseBody("ID không hợp lệ", 400)
                );
            }
            ReservationResponseDTO reservation = reservationService.getReservationById(id, authenticatedUser.getId());
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseBody("Đặt chỗ không tồn tại", 404)
            );
        } catch (Exception e) {
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
    public ResponseEntity<?> getMyReservationsWithBooks(
            @AuthenticationPrincipal User authenticatedUser) {
        try {
            List<java.util.Map<String, Object>> result =
                reservationService.getMyReservationsWithBooksDetail(authenticatedUser.getId());

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                new ErrorResponseBody(e.getMessage(), 400)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseBody("Lỗi máy chủ. Vui lòng thử lại sau.", 500)
            );
        }
    }

    // PUT /api/v1/reservations/{id} - Update reservation
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(
            @PathVariable Integer id,
            @RequestBody @Valid ReservationRequestDTO requestDTO,
            @AuthenticationPrincipal User authenticatedUser) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponseBody("ID không hợp lệ", 400)
                );
            }

            ReservationResponseDTO reservation = reservationService.updateReservation(id, authenticatedUser.getId(), requestDTO);

            return ResponseEntity.ok(reservation);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseBody("Đặt chỗ không tồn tại", 404)
            );
        } catch (Exception e) {
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
    public ResponseEntity<?> getReservationForAdmin(
            @PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponseBody("ID không hợp lệ", 400)
                );
            }

            java.util.Map<String, Object> result = reservationService.getReservationWithBooksDetail(id);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseBody("Đặt chỗ không tồn tại", 404)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseBody("Lỗi máy chủ. Vui lòng thử lại sau.", 500)
            );
        }
    }

    // DELETE /api/v1/reservations/{id} - Delete reservation
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(
            @PathVariable Integer id,
            @AuthenticationPrincipal User authenticatedUser) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponseBody("ID không hợp lệ", 400)
                );
            }
            reservationService.deleteReservation(id, authenticatedUser.getId());
            return ResponseEntity.ok("Đặt chỗ đã được xóa thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseBody("Đặt chỗ không tồn tại", 404)
            );
        } catch (Exception e) {
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
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
        List<ReservationResponseDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
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

    // PATCH /api/v1/reservations/{id}/status - Update status only (For Admin)
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatusByAdmin(
            @PathVariable Integer id,
            @RequestBody java.util.Map<String, String> body) {
        try {
            String newStatus = body.get("status");
            if (newStatus == null || newStatus.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponseBody("Thiếu trạng thái", 400));
            }

            reservationService.updateStatus(id, newStatus);

            // Trả về JSON báo thành công
            return ResponseEntity.ok(new java.util.HashMap<String, String>() {{
                put("message", "Cập nhật trạng thái thành công");
                put("status", newStatus);
            }});
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseBody(e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseBody("Lỗi máy chủ", 500));
        }
    }
}
