package com.library.server.repository;

import com.library.server.dto.response.LoanResponseDTO;
import com.library.server.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface LoanRepository extends JpaRepository<Loan, Integer> {
    // Tạm thời chỉ cần lấy tất cả (findAll) là đủ xài
    List<Loan> findByUserId(Integer userId);
}
