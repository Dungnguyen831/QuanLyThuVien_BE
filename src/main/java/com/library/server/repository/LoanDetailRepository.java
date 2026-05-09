package com.library.server.repository;

import com.library.server.entity.LoanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = "SELECT DATE_FORMAT(l.borrow_date, '%m/%Y') as month, COUNT(*) as count " +
            "FROM loan_details ld " +
            "JOIN loans l ON ld.loan_id = l.id " + // Cột nối có thể là l.id hoặc l.loan_id tùy DB của ông
            "WHERE l.borrow_date >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
            "GROUP BY month " +
            "ORDER BY MIN(l.borrow_date) ASC", nativeQuery = true)
    List<Object[]> getMonthlyBorrowingStats();

    @Query(value = "SELECT bc.id as bookCode, b.title, u.full_name as readerName, " +
            "l.borrow_date, ld.status " + // Sửa ld thành l ở đây
            "FROM loan_details ld " +
            "JOIN loans l ON ld.loan_id = l.id " +
            "JOIN users u ON l.user_id = u.id " +
            "JOIN book_copies bc ON ld.book_copy_id = bc.id " +
            "JOIN books b ON bc.book_id = b.id " +
            "ORDER BY l.borrow_date DESC LIMIT 5", nativeQuery = true) // Và sửa ld thành l ở đây
    List<Object[]> findRecentLoanDetails();

    @Query("select count(ld) > 0 from LoanDetail ld where ld.loan.user.id = :userId and ld.bookCopy.book.id = :bookId and ld.status = :status")
    boolean existsBorrowingByUserAndBook(@Param("userId") Integer userId,
                                         @Param("bookId") Integer bookId,
                                         @Param("status") String status);
}