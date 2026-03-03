package com.library.server.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Category extends BaseEntity {
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}