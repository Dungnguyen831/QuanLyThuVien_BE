package com.library.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "publishers")
@Getter
@Setter
public class Publisher extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String address;

    private String email;

    // Các trường id, createdAt, updatedAt đã được kế thừa từ BaseEntity
}