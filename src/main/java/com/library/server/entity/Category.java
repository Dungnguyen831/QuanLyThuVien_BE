package com.library.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
@Entity
@Table(name = "categories")
@Getter @Setter
public class Category extends BaseEntity {
    private String name;
    private String description;

    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER) // Dùng EAGER để tránh lỗi 500 Lazy
    @JsonIgnore
    private List<Book> books;
}