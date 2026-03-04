package com.library.server.repository;

import com.library.server.entity.LoanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanDetailRepository extends JpaRepository<LoanDetail, Integer> {
    // Tạm thời chỉ cần lấy tất cả (findAll) là đủ xài
}