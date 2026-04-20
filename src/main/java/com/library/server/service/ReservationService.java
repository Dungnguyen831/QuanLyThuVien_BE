package com.library.server.service;

import com.library.server.dto.request.ReservationRequestDTO;
import com.library.server.dto.response.ReservationResponseDTO;
import com.library.server.entity.Reservation;
import com.library.server.entity.Book;
import com.library.server.entity.User;
import com.library.server.repository.ReservationRepository;
import com.library.server.repository.BookRepository;
import com.library.server.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              UserRepository userRepository,
                              BookRepository bookRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    // Helper method: Convert Reservation Entity to ReservationResponseDTO
    private ReservationResponseDTO mapToDTO(Reservation reservation) {
        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .userId(reservation.getUser() != null ? reservation.getUser().getId() : null)
                .bookId(reservation.getBook() != null ? reservation.getBook().getId() : null)
                .reservationDate(reservation.getReservationDate())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }

    // Create a new reservation
    public ReservationResponseDTO createReservation(Integer authenticatedUserId, ReservationRequestDTO requestDTO) {
        // ✅ SECURITY: Use authenticated user ID from JWT, not from request body
        if (authenticatedUserId == null || authenticatedUserId <= 0) {
            logger.warn("Invalid authenticated user ID: {}", authenticatedUserId);
            throw new IllegalArgumentException("User ID không hợp lệ");
        }
        
        logger.info("User {} creating reservation for bookId: {}", authenticatedUserId, requestDTO.getBookId());
        
        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> {
                    logger.warn("Authenticated user not found: {}", authenticatedUserId);
                    return new RuntimeException("Người dùng không tồn tại");
                });

        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> {
                    logger.warn("Book not found: {}", requestDTO.getBookId());
                    return new RuntimeException("Không tìm thấy sách với ID: " + requestDTO.getBookId());
                });

        Reservation reservation = Reservation.builder()
                .user(user)
                .book(book)
                .reservationDate(requestDTO.getReservationDate())
                .status(requestDTO.getStatus())
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        logger.info("Reservation created successfully for user {}: ID {}", authenticatedUserId, savedReservation.getId());
        return mapToDTO(savedReservation);
    }

    // Get all reservations
    // ✅ REMOVED: This method is not exposed in Controller endpoint
    // FE uses getReservationsByUserId() with pagination or getReservationsByUserIdList() instead

    // Get reservation by ID
    public ReservationResponseDTO getReservationById(Integer id, Integer authenticatedUserId) {
        // ✅ SECURITY: Verify ownership - user can only access their own reservations
        if (id == null || id <= 0) {
            logger.warn("Invalid reservation ID: {}", id);
            throw new IllegalArgumentException("ID không hợp lệ");
        }
        
        if (authenticatedUserId == null || authenticatedUserId <= 0) {
            logger.warn("Invalid authenticated user ID: {}", authenticatedUserId);
            throw new IllegalArgumentException("User ID không hợp lệ");
        }
        
        logger.info("User {} retrieving reservation ID: {}", authenticatedUserId, id);
        
        // ✅ CRITICAL: Use findByIdAndUserId to ensure user owns this reservation
        Reservation reservation = reservationRepository.findByIdAndUserId(id, authenticatedUserId)
                .orElseThrow(() -> {
                    logger.warn("Reservation {} not found or user {} is not the owner", id, authenticatedUserId);
                    return new IllegalArgumentException("Đặt chỗ không tồn tại");
                });
        
        logger.info("User {} successfully retrieved reservation {}", authenticatedUserId, id);
        return mapToDTO(reservation);
    }

    // Update reservation
    public ReservationResponseDTO updateReservation(Integer id, Integer authenticatedUserId, ReservationRequestDTO requestDTO) {
        // ✅ SECURITY: Verify ID and user ID validity
        if (id == null || id <= 0) {
            logger.warn("Invalid reservation ID for update: {}", id);
            throw new IllegalArgumentException("ID không hợp lệ");
        }
        
        if (authenticatedUserId == null || authenticatedUserId <= 0) {
            logger.warn("Invalid authenticated user ID for update: {}", authenticatedUserId);
            throw new IllegalArgumentException("User ID không hợp lệ");
        }

        logger.info("=== START UPDATE RESERVATION ===");
        logger.info("Reservation ID: {}, User ID: {}", id, authenticatedUserId);
        logger.debug("Request data - BookID: {}, Date: {}, Status: {}",
            requestDTO.getBookId(), requestDTO.getReservationDate(), requestDTO.getStatus());

        // ✅ CRITICAL: Use findByIdAndUserId to verify OWNERSHIP
        Reservation reservation = reservationRepository.findByIdAndUserId(id, authenticatedUserId)
                .orElseThrow(() -> {
                    logger.warn("User {} attempted to update reservation {} that they don't own", authenticatedUserId, id);
                    return new IllegalArgumentException("Đặt chỗ không tồn tại");
                });
        logger.debug("Found reservation to update: ID={}", reservation.getId());

        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> {
                    logger.warn("Authenticated user not found: {}", authenticatedUserId);
                    return new IllegalArgumentException("Người dùng không tồn tại");
                });
        logger.debug("Found user: {}", user.getEmail());

        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> {
                    logger.warn("Book not found for update: {}", requestDTO.getBookId());
                    return new IllegalArgumentException("Sách không tồn tại");
                });
        logger.debug("Found book: {}", book.getTitle());

        // Update reservation data
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(requestDTO.getReservationDate());
        reservation.setStatus(requestDTO.getStatus());
        logger.debug("Updated reservation object with new values");

        try {
            Reservation updatedReservation = reservationRepository.save(reservation);
            logger.info("User {} successfully updated reservation {}", authenticatedUserId, id);
            logger.info("=== END UPDATE RESERVATION - SUCCESS ===");
            return mapToDTO(updatedReservation);
        } catch (Exception e) {
            logger.error("Error saving updated reservation: {}", e.getMessage(), e);
            logger.error("=== END UPDATE RESERVATION - FAILED ===");
            throw e;
        }
    }

    // Delete reservation
    public void deleteReservation(Integer id, Integer authenticatedUserId) {
        // ✅ SECURITY: Verify ID and user ID validity
        if (id == null || id <= 0) {
            logger.warn("Invalid reservation ID for deletion: {}", id);
            throw new IllegalArgumentException("ID không hợp lệ");
        }
        
        if (authenticatedUserId == null || authenticatedUserId <= 0) {
            logger.warn("Invalid authenticated user ID for deletion: {}", authenticatedUserId);
            throw new IllegalArgumentException("User ID không hợp lệ");
        }

        logger.info("User {} attempting to delete reservation ID: {}", authenticatedUserId, id);

        // ✅ CRITICAL: Use findByIdAndUserId to verify OWNERSHIP before deletion
        Reservation reservation = reservationRepository.findByIdAndUserId(id, authenticatedUserId)
                .orElseThrow(() -> {
                    logger.warn("User {} attempted to delete reservation {} that they don't own", authenticatedUserId, id);
                    return new IllegalArgumentException("Đặt chỗ không tồn tại");
                });
        
        logger.info("User {} deleting reservation: {}", authenticatedUserId, id);
        reservationRepository.delete(reservation);
        logger.info("User {} successfully deleted reservation: {}", authenticatedUserId, id);
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
            logger.warn("Invalid userId provided: {}", userId);
            throw new IllegalArgumentException("User ID không hợp lệ");
        }
        
        // Verify user exists (security check - should only reach here if user is from JWT token)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Attempted to access non-existent user: {}", userId);
                    return new IllegalArgumentException("Người dùng không tồn tại");
                });

        logger.info("Fetching all reservations for authenticated user: {} ({})", userId, user.getEmail());

        // Get reservations - Repository handles WHERE user_id = ?
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        
        logger.debug("Found {} reservations for user: {}", reservations.size(), userId);

        // Handle case when no reservations found
        if (reservations.isEmpty()) {
            logger.info("No reservations found for user: {}", userId);
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
            logger.warn("Invalid reservation ID for admin: {}", id);
            throw new IllegalArgumentException("ID không hợp lệ");
        }

        logger.info("Admin retrieving reservation ID: {}", id);

        // Get reservation without ownership check
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Reservation not found for admin: {}", id);
                    return new IllegalArgumentException("Đặt chỗ không tồn tại");
                });

        logger.info("Admin successfully retrieved reservation: {}", id);
        return mapToDTO(reservation);
    }

    /**
     * ✅ ADMIN ONLY: Get all reservations in the system
     * Used for admin management dashboard
     * @return List of all ReservationResponseDTO
     */
    public List<ReservationResponseDTO> getAllReservations() {
        logger.info("Admin fetching all reservations for system management");

        // Get all reservations from database
        List<Reservation> allReservations = reservationRepository.findAll();

        logger.debug("Found {} total reservations in system", allReservations.size());

        // Handle case when no reservations found
        if (allReservations.isEmpty()) {
            logger.info("No reservations found in system");
            return List.of();
        }

        // Convert to DTO
        return allReservations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}

