package com.library.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "authors")
@Getter
@Setter
public class Author extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String biography;

    // Lưu ý: Các trường id, createdAt, và updatedAt
    // đã được kế thừa từ BaseEntity nên không cần khai báo lại ở đây.
}