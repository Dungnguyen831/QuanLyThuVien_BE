package com.library.server.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "books")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Book extends BaseEntity {
    private String title;
    private String isbn;
    private Integer publishedYear;
    private Integer totalQty;
    private Integer availableQty;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore // Không in cả object category
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore // Không in cả object author
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore // Không in cả object publisher
    private Publisher publisher;

    @OneToMany(mappedBy = "book")
    @JsonIgnore
    private List<BookCopy> bookCopies;

    // --- CÁC PHƯƠNG THỨC JSONGETTER ĐỂ TRẢ VỀ ID ---

    @JsonGetter("category_id")
    public Integer getCategoryIdId() {
        return (category != null) ? category.getId() : null;
    }

    @JsonGetter("author_id")
    public Integer getAuthorIdId() {
        return (author != null) ? author.getId() : null;
    }

    @JsonGetter("publisher_id")
    public Integer getPublisherIdId() {
        return (publisher != null) ? publisher.getId() : null;
    }
}