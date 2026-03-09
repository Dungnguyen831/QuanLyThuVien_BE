package com.library.server.repository;

import com.library.server.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Integer> {
    // Tự động có các hàm: findAll, findById, save, deleteById
}