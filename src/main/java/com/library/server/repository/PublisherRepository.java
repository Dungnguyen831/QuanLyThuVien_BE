package com.library.server.repository;

import com.library.server.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Integer> {
    List<Publisher> findByNameContainingIgnoreCase(String name);
    // Tự động có các hàm: findAll, findById, save, deleteById
}