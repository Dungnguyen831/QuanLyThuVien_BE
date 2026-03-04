package com.library.server.service;

import com.library.server.dto.response.BookResponseDTO;
import com.library.server.entity.Book;
import com.library.server.entity.BookCopy;
import com.library.server.repository.BookCopyRepository;
import com.library.server.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {


    @Autowired
    private BookRepository bookRepository;

    // Chuyển đổi 1 Entity sang DTO
    private BookResponseDTO convertToDTO(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setPublishedYear(book.getPublishedYear());
        dto.setTotalQty(book.getTotalQty());
        dto.setAvailableQty(book.getAvailableQty());
        dto.setImageUrl(book.getImageUrl());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());

        // Lấy ID từ các quan hệ (nếu có)
        if (book.getCategory() != null) dto.setCategoryId(book.getCategory().getId());
        if (book.getAuthor() != null) dto.setAuthorId(book.getAuthor().getId());
        if (book.getPublisher() != null) dto.setPublisherId(book.getPublisher().getId());

        return dto;
    }
    // Lấy toàn bộ danh sách sách
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Lấy chi tiết một cuốn sách theo ID
    public Book getBookById(Integer id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + id));
    }

    // Tìm kiếm sách theo tên
    public List<Book> searchBooks(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
    @Autowired
    private BookCopyRepository bookCopyRepository;

    // Lấy toàn bộ bản sao của một cuốn sách
    public List<BookCopy> getCopiesByBookId(Integer bookId) {
        return bookCopyRepository.findByBookId(bookId);
    }

    // Lấy tất cả bản sao trong hệ thống (nếu cần)
    public List<BookCopy> getAllCopies() {
        return bookCopyRepository.findAll();
    }
}