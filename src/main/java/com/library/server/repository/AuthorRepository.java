package com.library.server.repository;

import com.library.server.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    // Mặc định đã có: findAll(), findById(), save(), deleteById()
}