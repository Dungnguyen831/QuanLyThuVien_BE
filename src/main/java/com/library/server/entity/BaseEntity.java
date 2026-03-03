package com.library.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass // Đánh dấu đây là class cha, không tạo thành bảng riêng dưới DB
@EntityListeners(AuditingEntityListener.class) // Lắng nghe sự kiện để tự động điền ngày giờ
public abstract class BaseEntity {

    @Id // JPA bắt buộc phải có @Id trong code, dù dưới DB bạn không set Khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}