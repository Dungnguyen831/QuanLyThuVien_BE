package com.library.server.repository;

import com.library.server.entity.LoanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface LoanDetailRepository extends JpaRepository<LoanDetail, Integer> {
    // Tạm thời chỉ cần lấy tất cả (findAll) là đủ xài
    List<LoanDetail> findByLoanId(Integer loanId);

    @Query("select count(ld) > 0 from LoanDetail ld where ld.loan.user.id = :userId and ld.bookCopy.book.id = :bookId and ld.status = :status")
    boolean existsBorrowingByUserAndBook(@Param("userId") Integer userId,
                                         @Param("bookId") Integer bookId,
                                         @Param("status") String status);
}