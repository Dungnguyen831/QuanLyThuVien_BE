package com.library.server.task;

import com.library.server.entity.Reservation;
import com.library.server.repository.ReservationRepository;
import com.library.server.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationAutoCancelTask {

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    /**
     * Tự động hủy đặt chỗ quá hạn (Chạy mỗi giờ một lần)
     * Dùng cron = "0 * * * * *" để test chạy mỗi 1 phút
     */
    @Scheduled(cron = "0 0 * * * *")
    public void autoCancelExpiredReservations() {
        log.info("Bắt đầu quét DB tìm đơn đặt chỗ quá hạn bằng LocalDate...");

        // Chỉ dùng LocalDate: Lấy ngày hiện tại, không quan tâm giờ phút giây
        LocalDate today = LocalDate.now();

        // Lùi ngày tương ứng
        LocalDate threeDaysAgo = today.minusDays(3);
        LocalDate oneDayAgo = today.minusDays(1);

        // Truy vấn xuống DB
        List<Reservation> expiredReservations = reservationRepository.findExpiredReservations(threeDaysAgo, oneDayAgo);

        int cancelCount = 0;

        for (Reservation reservation : expiredReservations) {
            try {
                // Gọi thẳng updateStatus để đổi trạng thái thành CANCELLED và trả lại sách
                reservationService.updateStatus(reservation.getId(), "cancelled");
                cancelCount++;
                log.info("Đã tự động hủy đặt chỗ ID: {} (Trạng thái: {})", reservation.getId(), reservation.getStatus());
            } catch (Exception e) {
                log.error("Lỗi khi tự động hủy đặt chỗ ID {}: {}", reservation.getId(), e.getMessage());
            }
        }

        if (cancelCount > 0) {
            log.info("Hoàn tất. Đã dọn dẹp và hủy thành công {} đơn đặt chỗ.", cancelCount);
        } else {
            log.info("Hoàn tất. Không có đơn đặt chỗ nào quá hạn.");
        }
    }
}