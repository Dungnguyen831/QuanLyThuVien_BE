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
        // 1. Kiểm tra sách tồn tại
        Book book = bookRepository.findById(template.getBook().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + template.getBook().getId()));

        // 2. Chuẩn bị phần tiền tố Barcode
        String bookPart = normalizeToInitials(book.getTitle());
        String categoryPart = (book.getCategory() != null) ? normalizeToInitials(book.getCategory().getName()) : "TL";
        String yearPart = (book.getPublishedYear() != null) ? book.getPublishedYear().toString() : "00";

        List<BookCopy> copies = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            BookCopy newCopy = new BookCopy();
            newCopy.setBook(book);
            newCopy.setConditionStatus(template.getConditionStatus());
            newCopy.setAvailabilityStatus(template.getAvailabilityStatus());

            // LƯU LẦN 1: Để lấy ID từ Database
            newCopy = bookCopyRepository.save(newCopy);

            // TẠO BARCODE: Kết hợp tiền tố + ID vừa sinh ra
            // Ví dụ: SHDGD2024-75 (75 là ID của bản sao trong DB)
            String customBarcode = String.format("%s%s%s-%d", bookPart, categoryPart, yearPart, newCopy.getId());
            newCopy.setBarcode(customBarcode);

            // LƯU LẦN 2: Cập nhật lại barcode chính thức
            copies.add(bookCopyRepository.save(newCopy));
        }

        // 3. Cập nhật số lượng vào bảng Books
        int currentTotal = (book.getTotalQty() == null ? 0 : book.getTotalQty());
        book.setTotalQty(currentTotal + quantity);

        // CHỈ CỘNG vào available_qty nếu trạng thái bản sao là AVAILABLE
        if ("AVAILABLE".equalsIgnoreCase(template.getAvailabilityStatus())) {
            int currentAvailable = (book.getAvailableQty() == null ? 0 : book.getAvailableQty());
            book.setAvailableQty(currentAvailable + quantity);
        }

        bookRepository.save(book);
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
    private String normalizeToInitials(String input) {
        if (input == null || input.trim().isEmpty()) return "X";

        // Tách chuỗi thành các từ
        String[] words = input.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                initials.append(word.charAt(0)); // Lấy chữ cái đầu
            }
        }

        // Khử dấu tiếng Việt và viết hoa
        String result = java.text.Normalizer.normalize(initials.toString(), java.text.Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

        return pattern.matcher(result).replaceAll("")
                .replaceAll("[^a-zA-Z0-9]", "")
                .toUpperCase();
    }

}