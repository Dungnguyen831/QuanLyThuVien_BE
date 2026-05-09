package com.library.server.repository;

import com.library.server.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByUserId(Integer userId);

    Page<Reservation> findByUserId(Integer userId, Pageable pageable);

    Optional<Reservation> findByIdAndUserId(Integer id, Integer userId);

    boolean existsByUserIdAndBookIdAndStatus(Integer userId, Integer bookId, String status);

    long countByUserIdAndStatusIn(Integer userId, List<String> statuses);

    @Query("SELECT r FROM Reservation r WHERE " +
            "(LOWER(r.status) = 'pending' AND CAST(r.createdAt AS date) <= :threeDaysAgo) OR " +
            "(LOWER(r.status) = 'approved' AND CAST(r.updatedAt AS date) <= :oneDayAgo)")
    List<Reservation> findExpiredReservations(
            @Param("threeDaysAgo") LocalDate threeDaysAgo,
            @Param("oneDayAgo") LocalDate oneDayAgo
    );
}

