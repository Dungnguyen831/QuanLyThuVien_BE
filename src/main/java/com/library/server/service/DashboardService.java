package com.library.server.service;

import com.library.server.dto.response.DashboardStatsDTO;
import com.library.server.repository.BookRepository;
import com.library.server.repository.LoanDetailRepository;
import com.library.server.repository.LoanRepository;
import com.library.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DashboardService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private LoanDetailRepository loanDetailRepository;

    public DashboardStatsDTO getStats() {
        long totalBooks = bookRepository.count();
        long loanBooks = loanDetailRepository.countByStatus("BORROWED");

        // Độc giả mới trong 30 ngày qua
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long newReaders = userRepository.countByCreatedAtAfter(thirtyDaysAgo);

        // Sách quá hạn
        long overdueBooks = loanDetailRepository.countByDueDateBeforeAndReturnDateIsNull(LocalDateTime.now());
        //
        System.out.println(">>> CHECK DB: totalBooks = " + totalBooks); // Thêm dòng này

        return new DashboardStatsDTO(totalBooks, loanBooks, newReaders, overdueBooks);
    }
}