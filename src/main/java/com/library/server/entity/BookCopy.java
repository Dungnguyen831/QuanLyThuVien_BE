package com.library.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book_copies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BookCopy extends BaseEntity {
    private String barcode;
    private String availabilityStatus;

    // Nối với bảng Book để lấy được tên sách (title)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Book book;
}