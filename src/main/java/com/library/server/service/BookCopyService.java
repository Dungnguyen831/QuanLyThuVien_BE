package com.library.server.service;

import com.library.server.entity.Book;
import com.library.server.entity.BookCopy;
import com.library.server.entity.Shelf;
import com.library.server.repository.BookCopyRepository;
import com.library.server.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookCopyService {

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private BookRepository bookRepository;

    public BookCopy createCopy(BookCopy copy) {
        return bookCopyRepository.save(copy);
    }

    public List<BookCopy> createMultipleCopiesManual(BookCopy template, int quantity) {
        List<BookCopy> copies = new ArrayList<>();

        // Đảm bảo book tồn tại trong DB
        Book book = bookRepository.findById(template.getBook().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + template.getBook().getId()));

        for (int i = 0; i < quantity; i++) {
            BookCopy newCopy = new BookCopy();
            newCopy.setBook(book);
            newCopy.setConditionStatus(template.getConditionStatus());
            newCopy.setAvailabilityStatus(template.getAvailabilityStatus());

            // Tạo barcode duy nhất bằng timestamp + số thứ tự i
            newCopy.setBarcode("BC" + System.currentTimeMillis() + i);

            copies.add(bookCopyRepository.save(newCopy));
        }
        return copies;

    }
    // Xóa bản sao
    public void deleteCopy(Integer id) {
        if (!bookCopyRepository.existsById(id)) {
            throw new RuntimeException("Bản sao không tồn tại với ID: " + id);
        }
        bookCopyRepository.deleteById(id);
    }
    // Cập nhật thông tin bản sao
    public BookCopy updateCopy(Integer id, BookCopy copyDetails) {
        // 1. Kiểm tra bản sao có tồn tại không
        BookCopy copy = bookCopyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản sao với ID: " + id));

        // 2. Cập nhật các trường thông tin cơ bản
        copy.setBarcode(copyDetails.getBarcode());
        copy.setConditionStatus(copyDetails.getConditionStatus());
        copy.setAvailabilityStatus(copyDetails.getAvailabilityStatus());

        // 3. Cập nhật quan hệ (Book và Shelf) nếu có truyền vào
        if (copyDetails.getBook() != null) {
            copy.setBook(copyDetails.getBook());
        }
        if (copyDetails.getShelf() != null) {
            copy.setShelf(copyDetails.getShelf());
        }

        // 4. Lưu lại (Trường updatedAt sẽ tự động cập nhật nhờ BaseEntity)
        return bookCopyRepository.save(copy);
    }
    //lấy bản sao theo book_id
    public List<BookCopy> getCopiesByBookId(Integer bookId) {
        return bookCopyRepository.findByBookId(bookId);
    }
}