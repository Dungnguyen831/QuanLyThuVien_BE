package com.library.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;

import java.util.List;

@Entity
@Table(name = "authors")
@Getter @Setter
public class Author extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String biography;


    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Book> books;

    // ✅ Calculated field - không lưu vào DB, tính từ books.size()

}