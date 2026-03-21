package com.library.server.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "book_copies")
@Getter @Setter
public class BookCopy extends BaseEntity {

    private String barcode;

    @Column(name = "condition_status")
    private String conditionStatus; // Ví dụ: NEW, GOOD, DAMAGED

    @Column(name = "availability_status")
    private String availabilityStatus; // Ví dụ: AVAILABLE, BORROWED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id")
    @JsonIgnore
    private Shelf shelf;

    @JsonGetter("book_id")
    public Integer getBookIdJson() {
        return book != null ? book.getId() : null;
    }

    @JsonGetter("shelf_id")
    public Integer getShelfIdJson() {
        return shelf != null ? shelf.getId() : null;
    }
}