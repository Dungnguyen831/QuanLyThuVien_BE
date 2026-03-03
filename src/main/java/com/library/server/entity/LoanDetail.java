package com.library.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_details")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoanDetail extends BaseEntity {
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private String status;

    // Nối với phiếu mượn gốc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Loan loan;

    // Nối với cuốn sách vật lý (mã vạch)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private BookCopy bookCopy;
}