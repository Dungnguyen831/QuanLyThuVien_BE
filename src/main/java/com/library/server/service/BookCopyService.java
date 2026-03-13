package com.library.server.service;

import com.library.server.entity.Book;
import com.library.server.entity.BookCopy;
import com.library.server.entity.Shelf;
import com.library.server.repository.BookCopyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookCopyService {

    @Autowired
    private BookCopyRepository bookCopyRepository;

    // Tạo bản sao mới
    public BookCopy createCopy(BookCopy copy) {
        return bookCopyRepository.save(copy);
    }
    //tạo bản sao mới theo số lượng
    public List<BookCopy> createMultipleCopiesManual(BookCopy template, int quantity) {
        List<BookCopy> savedList = new ArrayList<>();

        // Lấy sẵn Book và Shelf từ template ra ngoài cho chắc
        Book bookFromRequest = template.getBook();
        Shelf shelfFromRequest = template.getShelf();

        for (int i = 1; i <= quantity; i++) {
            try {
                BookCopy newCopy = new BookCopy();

                // PHẢI GÁN VÀO ĐỐI TƯỢNG MỚI (newCopy)
                newCopy.setBook(bookFromRequest);
                newCopy.setShelf(shelfFromRequest);

                newCopy.setConditionStatus(template.getConditionStatus());
                newCopy.setAvailabilityStatus(template.getAvailabilityStatus());
                newCopy.setBarcode(template.getBarcode() + "-" + i + "-" + System.nanoTime());

                // Lưu đối tượng đã được gán đầy đủ
                savedList.add(bookCopyRepository.save(newCopy));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return savedList;
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
}