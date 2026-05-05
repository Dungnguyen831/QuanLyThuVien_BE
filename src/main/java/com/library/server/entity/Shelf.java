package com.library.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "shelves")
@Getter
@Setter
public class Shelf extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private Integer floor; // Số tầng (ví dụ: tầng 1, tầng 2)

    // id, createdAt, updatedAt đã được kế thừa từ BaseEntity

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}