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
        return convertToResponeDTO(authorRepository.save(author));
    }
    public AuthorResponseDTO updateAuthor(Integer id, AuthorRequestDTO requestDTO){
        Author author = authorRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả: " + id));
        author.setName(requestDTO.getName());
        author.setBiography(requestDTO.getBiography());
        return convertToResponeDTO(authorRepository.save(author));
    }
    public void deleteAuthor(Integer id){
        if (!authorRepository.existsById(id)) {
            throw new RuntimeException("Tác giả không tồn tại");
        }
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

