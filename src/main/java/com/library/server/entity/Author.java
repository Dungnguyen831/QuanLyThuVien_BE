package com.library.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "authors")
@Getter @Setter
public class Author extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private Integer bookCount;

    @Column(nullable = false)
    private String Status;

}