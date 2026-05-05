package com.library.server.repository;

import com.library.server.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    
    /**
     * Find all reservations for a specific user
     */
    List<Reservation> findByUserId(Integer userId);
    
    /**
     * Find all reservations for a specific user with pagination
     */
    Page<Reservation> findByUserId(Integer userId, Pageable pageable);
    
    /**
     * ✅ SECURITY: Find reservation by ID AND user ID (ownership verification)
     * Ensures: WHERE id = ? AND user_id = ?
     * This prevents users from accessing other users' reservations
     */
    Optional<Reservation> findByIdAndUserId(Integer id, Integer userId);
    
    /**
     * Check if a user already has a reservation for the same book with a specific status
     */
    boolean existsByUserIdAndBookIdAndStatus(Integer userId, Integer bookId, String status);

    /**
     * Count reservations for a user by a list of statuses
     */
    long countByUserIdAndStatusIn(Integer userId, List<String> statuses);
}

