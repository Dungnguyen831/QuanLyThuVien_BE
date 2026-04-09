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

    @Column(name = "bookcount", nullable = false)
    private Integer bookcount = 0; // Gán giá trị mặc định là 0 ngay tại đây

    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER) // Dùng EAGER để tránh lỗi 500 Lazy
    @JsonIgnore
    private List<Book> books;

    // Jackson sẽ ưu tiên hàm này để gửi về Front-end
    @JsonProperty("bookCount")
    public int getCalculatedBookCount() {
        return (books != null) ? books.size() : 0;
    }
}