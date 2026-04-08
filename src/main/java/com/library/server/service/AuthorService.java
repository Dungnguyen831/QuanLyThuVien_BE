package com.library.server.service;

import com.library.server.entity.Author;
import com.library.server.repository.AuthorRepository;
import com.library.server.dto.request.AuthorRequestDTO;
import com.library.server.dto.response.AuthorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;
    // Lấy danh sách tất cả tác giả
    public List<AuthorResponseDTO> getAllAuthors() {
        return authorRepository.findAll().stream().map(this::convertToResponeDTO).collect(Collectors.toList());
    }
    public AuthorResponseDTO getAuthorById(Integer id){
        Author author = authorRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả: " + id));
        return convertToResponeDTO(author);
    }
    public AuthorResponseDTO createAuthor(AuthorRequestDTO requestDTO){
        Author author = new Author();
        author.setName(requestDTO.getName());
        author.setBiography(requestDTO.getBiography());
        author.setCountry(requestDTO.getCountry() != null ? requestDTO.getCountry() : "Unknown");

        // Kiểm tra nếu null thì gán bằng 0, không để Hibernate nhìn thấy chữ Null
        author.setBookcount(requestDTO.getBookcount() != null ? requestDTO.getBookcount() : 0);

        author.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : "Đang hợp tác");
        return convertToResponeDTO(authorRepository.save(author));
    }
    public AuthorResponseDTO updateAuthor(Integer id, AuthorRequestDTO requestDTO){
        Author author = authorRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả: " + id));
        author.setName(requestDTO.getName());
        author.setBiography(requestDTO.getBiography());
        author.setBookcount(requestDTO.getBookcount());
        author.setStatus(requestDTO.getStatus());
        author.setCountry(requestDTO.getCountry());
        return convertToResponeDTO(authorRepository.save(author));
    }
    public void deleteAuthor(Integer id) {
        // 1. Tìm tác giả, nếu không có thì báo lỗi luôn
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tác giả không tồn tại"));

        // 2. KIỂM TRA RÀNG BUỘC: Nếu danh sách sách của tác giả này không trống
        // Lưu ý: Đảm bảo trong Entity Author.java ông đã định nghĩa:
        // @OneToMany(mappedBy = "author") private List<Book> books;
        if (author.getBooks() != null && !author.getBooks().isEmpty()) {
            throw new RuntimeException("Không thể xóa: Tác giả này hiện đang có "
                    + author.getBooks().size() + " cuốn sách trong hệ thống!");
        }

        // 3. Nếu không vướng sách nào thì mới cho xóa
        authorRepository.deleteById(id);
    }
    public List<AuthorResponseDTO> search(String name) {
        return authorRepository.findByNameContainingIgnoreCase(name).stream()
                .map(author -> {
                    AuthorResponseDTO dto = new AuthorResponseDTO();
                    BeanUtils.copyProperties(author, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    private AuthorResponseDTO convertToResponeDTO(Author author){
    AuthorResponseDTO dto = new AuthorResponseDTO();
    BeanUtils.copyProperties(author, dto);
    return dto;
    }
}

