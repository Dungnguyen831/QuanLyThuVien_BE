package com.library.server.service;

import com.library.server.dto.request.BookRequestDTO;
import com.library.server.entity.*;
import com.library.server.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {
    @Autowired private BookRepository bookRepository;
    @Autowired private BookCopyRepository bookCopyRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private PublisherRepository publisherRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Integer id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + id));
    }

    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<BookCopy> getCopiesByBookId(Integer bookId) {
        return bookCopyRepository.findByBookId(bookId);
    }

    // --- PHẦN SỬA ĐỔI CHÍNH ---

    // Thêm mới sách bằng DTO
    public Book createBook(BookRequestDTO dto) {
        Book book = new Book();
        mapDtoToEntity(dto, book);
        return bookRepository.save(book);
    }

    // Cập nhật sách bằng DTO
    public Book updateBook(Integer id, BookRequestDTO dto) {
        Book book = getBookById(id);
        mapDtoToEntity(dto, book);
        return bookRepository.save(book);
    }

    // Hàm dùng chung để chuyển dữ liệu từ DTO sang Entity
    private void mapDtoToEntity(BookRequestDTO dto, Book book) {
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setPublishedYear(dto.getPublishedYear());
        book.setTotalQty(dto.getTotalQty());
        book.setAvailableQty(dto.getAvailableQty());
        book.setImageUrl(dto.getImageUrl());

        // Tìm các Object từ ID và gán vào Entity
        if (dto.getCategoryId() != null) {
            book.setCategory(categoryRepository.findById(dto.getCategoryId()).orElse(null));
        }
        if (dto.getAuthorId() != null) {
            book.setAuthor(authorRepository.findById(dto.getAuthorId()).orElse(null));
        }
        if (dto.getPublisherId() != null) {
            book.setPublisher(publisherRepository.findById(dto.getPublisherId()).orElse(null));
        }
    }

    public void deleteBook(Integer id) {
        Book book = getBookById(id);
        bookRepository.delete(book);
    }
}