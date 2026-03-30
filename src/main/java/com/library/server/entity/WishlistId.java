package com.library.server.entity;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WishlistId implements Serializable {
    private Integer userId;
    private Integer bookId;
}

