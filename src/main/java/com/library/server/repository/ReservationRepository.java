package com.library.server.repository;

import com.library.server.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}

