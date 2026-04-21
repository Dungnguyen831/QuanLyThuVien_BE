package com.library.server.service;

import com.library.server.dto.request.BookCopyRequestDTO;
import com.library.server.entity.Book;
import com.library.server.entity.BookCopy;
import com.library.server.entity.Shelf;
import com.library.server.repository.BookCopyRepository;
import com.library.server.repository.BookRepository;
import com.library.server.repository.ShelfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookCopyService {

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private BookRepository bookRepository;
    private final ShelfRepository shelfRepository; // <--- CẦN THÊM DÒNG NÀY ĐỂ HẾT LỖI ĐỎ

    public BookCopyService(ShelfRepository shelfRepository) {
        this.shelfRepository = shelfRepository;
    }

    public BookCopy createCopy(BookCopy copy) {
        return bookCopyRepository.save(copy);
    }

    @Transactional // Quan trọng: Đảm bảo tính nhất quán dữ liệu
    public List<BookCopy> createMultipleCopiesManual(BookCopy template, int quantity) {
        Book book = bookRepository.findById(template.getBook().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + template.getBook().getId()));

        List<BookCopy> copies = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            BookCopy newCopy = new BookCopy();
            newCopy.setBook(book);
            newCopy.setConditionStatus(template.getConditionStatus());
            newCopy.setAvailabilityStatus(template.getAvailabilityStatus());
            newCopy.setBarcode("BC" + System.currentTimeMillis() + i);
            copies.add(bookCopyRepository.save(newCopy));
        }

        // Tự động cộng dồn số lượng vào Book
        int newTotal = (book.getTotalQty() == null ? 0 : book.getTotalQty()) + quantity;
        book.setTotalQty(newTotal);
        int newavailable = (book.getAvailableQty() == null ? 0 : book.getAvailableQty()) + quantity;
        book.setAvailableQty(newavailable);

        bookRepository.save(book); // Lưu thay đổi vào bảng books
        return copies;
    }
    @Transactional
    public void deleteCopy(Integer id) {
        BookCopy copy = bookCopyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bản sao không tồn tại với ID: " + id));

        Book book = copy.getBook();

        // Giảm số lượng tổng
        if (book.getTotalQty() != null && book.getTotalQty() > 0) {
            book.setTotalQty(book.getTotalQty() - 1);
        }

        // Giảm số lượng sẵn có nếu bản sao đang ở trạng thái AVAILABLE
        if ("AVAILABLE".equalsIgnoreCase(copy.getAvailabilityStatus())
                && book.getAvailableQty() != null && book.getAvailableQty() > 0) {
            book.setAvailableQty(book.getAvailableQty() - 1);
        }

        bookCopyRepository.delete(copy);
        bookRepository.save(book);
    }
    // Cập nhật thông tin bản sao
    public BookCopy updateCopy(Integer id, BookCopyRequestDTO dto) {
        // 1. Tìm bản sao cũ trong DB
        BookCopy copy = bookCopyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản sao với ID: " + id));

        // 2. Cập nhật thông tin cơ bản (Chỉ cập nhật nếu DTO có dữ liệu, tránh ghi đè null)
        if (dto.getBarcode() != null) copy.setBarcode(dto.getBarcode());
        if (dto.getConditionStatus() != null) copy.setConditionStatus(dto.getConditionStatus());
        if (dto.getAvailabilityStatus() != null) copy.setAvailabilityStatus(dto.getAvailabilityStatus());

        // 3. Xử lý Shelf (Chuyển từ shelfId sang Object Shelf)
        if (dto.getShelfId() != null) {
            Shelf shelf = shelfRepository.findById(dto.getShelfId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy kệ"));
            copy.setShelf(shelf);
        }

        return bookCopyRepository.save(copy);
    }
    //lấy bản sao theo book_id
    public List<BookCopy> getCopiesByBookId(Integer bookId) {
        return bookCopyRepository.findByBookId(bookId);
    }
}