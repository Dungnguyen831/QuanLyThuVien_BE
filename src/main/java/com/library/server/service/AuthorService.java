package com.library.server.service;

import com.library.server.entity.Author;
import com.library.server.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    // Lấy danh sách tất cả tác giả
    public List<Author> getAll() {
        return authorRepository.findAll();
    }

    // Lấy chi tiết 1 tác giả theo ID
    public Optional<Author> getById(Integer id) {
        return authorRepository.findById(id);
    }

    // Tìm kiếm tác giả
    public List<Author> search(String name) {
        return authorRepository.findByNameContainingIgnoreCase(name);
    }
    //Cập nhật tác giả
    public Author save(Author author){
        return authorRepository.save(author);
    }
    // Xóa tác giả
    public void delete(Integer id) {
        authorRepository.deleteById(id);
    }
}