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

    @Autowired
    private BookCopyRepository bookCopyRepository;
    //hiển thị tất cả danh sách
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    //hiển thị sách theo id
    public Book getBookById(Integer id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + id));
    }
    // tìm kiếm sách theo tên
    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
    // tìm kiếm sách theo barcode
    public List<Book> searchBooksByBarcode(String isbn) {
        return bookRepository.findByIsbnContainingIgnoreCase(isbn);
    }
    // hiển thị danh sách bản sao của sách theo id sách
    public List<BookCopy> getCopiesByBookId(Integer bookId) {
        return bookCopyRepository.findByBookId(bookId);
    }

    // Thêm mới sách
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }
    // Cập nhật sách
    public Book updateBook(Integer id, Book bookDetails) {
        Book book = getBookById(id);
        book.setTitle(bookDetails.getTitle());
        book.setIsbn(bookDetails.getIsbn());
        book.setPublishedYear(bookDetails.getPublishedYear());
        book.setTotalQty(bookDetails.getTotalQty());
        book.setAvailableQty(bookDetails.getAvailableQty());
        book.setImageUrl(bookDetails.getImageUrl());

        // Cập nhật các mối quan hệ nếu có truyền vào
        if (bookDetails.getCategory() != null) book.setCategory(bookDetails.getCategory());
        if (bookDetails.getAuthor() != null) book.setAuthor(bookDetails.getAuthor());
        if (bookDetails.getPublisher() != null) book.setPublisher(bookDetails.getPublisher());

        return bookRepository.save(book);
    }
    // Xóa sách
    public void deleteBook(Integer id) {
        Book book = getBookById(id);
        bookRepository.delete(book);
    }
}