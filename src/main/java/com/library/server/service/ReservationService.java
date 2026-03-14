package com.library.server.service;

import com.library.server.dto.request.ReservationRequestDTO;
import com.library.server.dto.response.ReservationResponseDTO;
import com.library.server.entity.Reservation;
import com.library.server.entity.Book;
import com.library.server.entity.User;
import com.library.server.repository.ReservationRepository;
import com.library.server.repository.BookRepository;
import com.library.server.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

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
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt chỗ với ID: " + id));

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + requestDTO.getUserId()));

        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + requestDTO.getBookId()));

        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(requestDTO.getReservationDate());
        reservation.setStatus(requestDTO.getStatus());

        Reservation updatedReservation = reservationRepository.save(reservation);
        return mapToDTO(updatedReservation);
    }

    // Delete reservation
    public void deleteReservation(Integer id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt chỗ với ID: " + id));
        reservationRepository.delete(reservation);
    }
}

