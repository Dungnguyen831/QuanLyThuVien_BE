package com.library.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "shelves")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shelf extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "floor")
    private Integer floor;

    @OneToMany(mappedBy = "shelf")
    @JsonIgnore
    private List<BookCopy> bookCopies;
}