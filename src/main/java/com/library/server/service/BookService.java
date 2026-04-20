package com.library.server.service;

import com.library.server.dto.request.BookRequestDTO; // Nhận dữ liệu thô từ Frontend
import com.library.server.entity.Author;
import com.library.server.entity.Book;
import com.library.server.entity.Category;
import com.library.server.entity.Publisher;
import com.library.server.repository.AuthorRepository;
import com.library.server.repository.BookRepository;
import com.library.server.repository.CategoryRepository;
import com.library.server.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    /**
     * Lấy tất cả sách để hiển thị lên bảng.
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    //lấy sách theo id
    public Book getBookById(Integer id) {

        if (id == null) {
            throw new RuntimeException("Lỗi: ID sách truyền vào bị rỗng (null)!");
            // Hoặc nếu bạn không muốn ném lỗi mà chỉ muốn trả về null thì dùng:
            // return null;
        }

        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + id));
    }
    /**
     * Hàm tạo sách mới: Chuyển đổi từ DTO (chứa ID) sang Entity (chứa Object).
     * @param dto Đối tượng chứa dữ liệu từ form Frontend gửi lên.
     */
    @Transactional
    public Book createBook(BookRequestDTO dto) {
        // 1. Khởi tạo đối tượng Book mới
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setPublishedYear(dto.getPublishedYear());
        book.setImageUrl(dto.getImageUrl());
        book.setDescription(dto.getDescription());

        // Mặc định số lượng là 0 nếu không truyền vào
        book.setTotalQty(dto.getTotalQty() != null ? dto.getTotalQty() : 0);
        book.setAvailableQty(dto.getAvailableQty() != null ? dto.getAvailableQty() : 0);

        // 2. Tìm kiếm và gán đối tượng Category từ categoryId
        if (dto.getCategoryId() != null) {
            Category cat = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Thể loại ID: " + dto.getCategoryId()));
            book.setCategory(cat);
        }

        // 3. Tìm kiếm và gán đối tượng Author từ authorId
        if (dto.getAuthorId() != null) {
            Author aut = authorRepository.findById(dto.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Tác giả ID: " + dto.getAuthorId()));
            book.setAuthor(aut);
        }

        // 4. Tìm kiếm và gán đối tượng Publisher từ publisherId
        if (dto.getPublisherId() != null) {
            Publisher pub = publisherRepository.findById(dto.getPublisherId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy NXB ID: " + dto.getPublisherId()));
            book.setPublisher(pub);
        }

        // 5. Lưu xuống Database
        return bookRepository.save(book);
    }

    //  hàm update
    @Transactional
    public Book updateBook(Integer id, BookRequestDTO dto) {
        // 1. Tìm sách cần sửa
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + id));

        // 2. Cập nhật các trường cơ bản
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setPublishedYear(dto.getPublishedYear());
        book.setImageUrl(dto.getImageUrl());
        book.setTotalQty(dto.getTotalQty() != null ? dto.getTotalQty() : book.getTotalQty());
        book.setAvailableQty(dto.getAvailableQty() != null ? dto.getAvailableQty() : book.getAvailableQty());
        book.setDescription(dto.getDescription());

        // 3. Cập nhật các khóa ngoại (Foreign Keys)
        if (dto.getCategoryId() != null) {
            Category cat = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            book.setCategory(cat);
        }
        if (dto.getAuthorId() != null) {
            Author aut = authorRepository.findById(dto.getAuthorId()).orElse(null);
            book.setAuthor(aut);
        }
        if (dto.getPublisherId() != null) {
            Publisher pub = publisherRepository.findById(dto.getPublisherId()).orElse(null);
            book.setPublisher(pub);
        }


        // 4. Lưu lại
        return bookRepository.save(book);
    }

    /**
     * Xóa sách theo ID.
     */
    public void deleteBook(Integer id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Sách không tồn tại để xóa!");
        }
        bookRepository.deleteById(id);
    }
}