package com.library.server.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_copies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BookCopy extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
    private Shelf shelf;

    private String barcode;
    private String conditionStatus;
    private String availabilityStatus;

    @JsonGetter("book_id")
    public Integer getBookIdId() {
        return (book != null) ? book.getId() : null;
    }

    @JsonGetter("shelf_id")
    public Integer getShelfIdId() {
        return (shelf != null) ? shelf.getId() : null;
    }
}