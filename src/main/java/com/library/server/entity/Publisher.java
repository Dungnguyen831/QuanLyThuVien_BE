package com.library.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "publishers")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Publisher extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "publisher")
    @JsonIgnore
    private List<Book> books;
}