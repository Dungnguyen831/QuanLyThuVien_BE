package com.library.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
@Entity
@Table(name = "categories")
@Getter @Setter
public class Category extends BaseEntity {
    private String name;
    private String description;
}