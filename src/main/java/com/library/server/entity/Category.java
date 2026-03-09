package com.library.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category extends BaseEntity { // Kế thừa từ BaseEntity của bạn

    private String name;

    private String description;

    // Các trường id, createdAt, updatedAt đã được BaseEntity xử lý
}