package com.library.server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    // Không khai báo 'id' vì đã có trong BaseEntity

    @Column(length = 50)
    private String name;

}