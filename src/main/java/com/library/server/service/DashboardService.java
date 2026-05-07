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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<Map<String, Object>> getMonthlyStats() {
        List<Object[]> results = loanDetailRepository.getMonthlyBorrowingStats();
        List<Map<String, Object>> chartData = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("label", row[0]); // Tháng (ví dụ: 05/2026)
            map.put("value", row[1]); // Số lượng
            chartData.add(map);
        }
        return chartData;
}
}