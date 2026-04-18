package com.library.server.repository;

import com.library.server.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FineRepository extends JpaRepository<Fine, Integer> {
    // Tìm tất cả khoản phạt của 1 người
    List<Fine> findByUserId(Integer userId);

    // Đếm xem người này đang có bao nhiêu khoản CHƯA ĐÓNG (is_paid = false)
    long countByUserIdAndIsPaidFalse(Integer userId);
}