package com.library.server.service;

import com.library.server.entity.BookCopy;
import com.library.server.repository.BookCopyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookCopyService {

    @Autowired
    private BookCopyRepository bookCopyRepository;

    // Tạo bản sao mới
    public BookCopy createCopy(BookCopy copy) {
        return bookCopyRepository.save(copy);
    }

    // Xóa bản sao
    public void deleteCopy(Integer id) {
        if (!bookCopyRepository.existsById(id)) {
            throw new RuntimeException("Bản sao không tồn tại với ID: " + id);
        }
        bookCopyRepository.deleteById(id);
    }
}