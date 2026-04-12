package com.library.server.service;

import com.library.server.dto.request.FineRequestDTO;
import com.library.server.dto.response.FineResponseDTO;
import com.library.server.entity.Fine;
import com.library.server.entity.LoanDetail;
import com.library.server.entity.User;
import com.library.server.repository.FineRepository;
import com.library.server.repository.LoanDetailRepository;
import com.library.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FineService {

    private final FineRepository fineRepository;
    private final UserRepository userRepository;
    private final LoanDetailRepository loanDetailRepository;

    // 1. Lấy tất cả danh sách phạt (Cho Admin Dashboard)
    public List<FineResponseDTO> getAllFines() {
        return fineRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // 2. Tạo phiếu phạt thủ công (Rách, Mất sách...)
    @Transactional
    public void createManualFine(FineRequestDTO request) {
        LoanDetail loanDetail = loanDetailRepository.findById(request.getLoanDetailId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu chi tiết mượn sách!"));

        User user = loanDetail.getLoan().getUser();
        Fine fine = new Fine();
        fine.setUser(user);
        fine.setLoanDetail(loanDetail);
        fine.setAmount(request.getAmount());
        fine.setReason(request.getReason());
        fine.setIsPaid(false); // Mặc định là chưa thanh toán

        fineRepository.save(fine);
    }

    // 3. Nghiệp vụ: Xác nhận thu tiền phạt
    @Transactional
    public void payFine(Integer fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biên bản phạt!"));

        if (fine.getIsPaid()) {
            throw new RuntimeException("Khoản phạt này đã được thanh toán trước đó rồi!");
        }

        // Đánh dấu đã thanh toán và lưu lại
        fine.setIsPaid(true);
        // fine.setUpdatedAt(LocalDateTime.now()); // Bỏ comment nếu Entity Fine của bạn không dùng @UpdateTimestamp

        fineRepository.save(fine);
    }

    // Hàm phụ: Map từ Entity sang DTO
    private FineResponseDTO mapToDTO(Fine fine) {
        FineResponseDTO dto = new FineResponseDTO();
        dto.setId(fine.getId());
        dto.setUserId(fine.getUser().getId());
        dto.setUserName(fine.getUser().getFullName());
        dto.setLoanDetailId(fine.getLoanDetail().getId());
        dto.setBookTitle(fine.getLoanDetail().getBookCopy().getBook().getTitle());
        dto.setAmount(fine.getAmount());
        dto.setReason(fine.getReason());
        dto.setIsPaid(fine.getIsPaid());
        dto.setCreatedAt(fine.getCreatedAt());
        return dto;
    }
}