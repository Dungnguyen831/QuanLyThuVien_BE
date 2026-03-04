package com.library.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "authors")
@Getter
@Setter
public class Author extends BaseEntity {
    private String name;
    @Column(columnDefinition = "TEXT")
    private String biography;

    @OneToMany(mappedBy = "author")
    @com.fasterxml.jackson.annotation.JsonIgnore // Tránh lặp vô tận JSON
    private List<Book> books;
}