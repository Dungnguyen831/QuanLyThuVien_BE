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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + requestDTO.getUserId()));

        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + requestDTO.getBookId()));

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
    public List<ReservationResponseDTO> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get reservation by ID
    public ReservationResponseDTO getReservationById(Integer id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt chỗ với ID: " + id));
        return mapToDTO(reservation);
    }

    // Update reservation
    public ReservationResponseDTO updateReservation(Integer id, ReservationRequestDTO requestDTO) {
        if (id == null || id <= 0) {
            logger.warn("Invalid reservation ID for update: {}", id);
            throw new IllegalArgumentException("ID không hợp lệ");
        }

        logger.info("Updating reservation ID: {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Reservation not found for update: {}", id);
                    return new IllegalArgumentException("Đặt chỗ không tồn tại");
                });

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> {
                    logger.warn("User not found for update: {}", requestDTO.getUserId());
                    return new IllegalArgumentException("Người dùng không tồn tại");
                });

        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> {
                    logger.warn("Book not found for update: {}", requestDTO.getBookId());
                    return new IllegalArgumentException("Sách không tồn tại");
                });

        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(requestDTO.getReservationDate());
        reservation.setStatus(requestDTO.getStatus());

        Reservation updatedReservation = reservationRepository.save(reservation);
        logger.info("Reservation updated successfully: {}", id);
        return mapToDTO(updatedReservation);
    }

    // Delete reservation
    public void deleteReservation(Integer id) {
        if (id == null || id <= 0) {
            logger.warn("Invalid reservation ID for deletion: {}", id);
            throw new IllegalArgumentException("ID không hợp lệ");
        }

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Reservation not found for deletion: {}", id);
                    return new IllegalArgumentException("Đặt chỗ không tồn tại");
                });
        
        logger.info("Deleting reservation: {}", id);
        reservationRepository.delete(reservation);
        logger.info("Reservation deleted successfully: {}", id);
    }

    /**
     * Get all reservations for a specific user with pagination
     * @param userId User ID
     * @param pageable Pagination information (page, size, sort)
     * @return Page of ReservationResponseDTO
     */
    public Page<ReservationResponseDTO> getReservationsByUserId(Integer userId, Pageable pageable) {
        // Validate user exists
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID không hợp lệ");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        // Get paginated reservations
        Page<Reservation> reservations = reservationRepository.findByUserId(userId, pageable);
        
        // Handle case when no reservations found
        if (reservations.isEmpty()) {
            return Page.empty(pageable);
        }

        // Convert to DTO
        return reservations.map(this::mapToDTO);
    }

    /**
     * Get all reservations for a specific user without pagination
     * @param userId User ID
     * @return List of ReservationResponseDTO
     */
    public List<ReservationResponseDTO> getReservationsByUserIdList(Integer userId) {
        // Validate user exists
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID không hợp lệ");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        // Get reservations
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
}

