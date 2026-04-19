package com.library.server.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "books")
@Getter @Setter
public class Book extends BaseEntity {
    private String title;
    private String isbn;
    private Integer publishedYear;
    private Integer totalQty;
    private Integer availableQty;
    private String imageUrl;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @JsonIgnore
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    @JsonIgnore
    private Publisher publisher;

    // Chỉ trả về ID khi render JSON
    @JsonGetter("category_id")
    public Integer getCategoryIdId() { return category != null ? category.getId() : null; }

    @JsonGetter("author_id")
    public Integer getAuthorIdId() { return author != null ? author.getId() : null; }

    @JsonGetter("category_name")
    public String getCategoryName() {
        return category != null ? category.getName() : null;
    }

    @JsonGetter("publisher_id")
    public Integer getPublisherIdId() { return publisher != null ? publisher.getId() : null; }


}