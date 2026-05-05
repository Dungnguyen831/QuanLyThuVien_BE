package com.library.server.repository;

import com.library.server.entity.LoanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanDetailRepository extends JpaRepository<LoanDetail, Integer> {
    // Tạm thời chỉ cần lấy tất cả (findAll) là đủ xài
    List<LoanDetail> findByLoanId(Integer loanId);
    long countByStatus(String Status);
    long countByDueDateBeforeAndReturnDateIsNull(LocalDateTime dateTime);
}