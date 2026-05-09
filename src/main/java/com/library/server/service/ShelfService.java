package com.library.server.service;

import com.library.server.dto.request.ShelfRequestDTO;
import com.library.server.dto.response.ShelfResponseDTO;
import com.library.server.entity.Category;
import com.library.server.entity.Shelf;
import com.library.server.repository.CategoryRepository;
import com.library.server.repository.ShelfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShelfService {

    private final ShelfRepository shelfRepository;
    private final CategoryRepository categoryRepository;

    public List<ShelfResponseDTO> getAllShelves() {
        return shelfRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public ShelfResponseDTO getShelfById(Integer id) {
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kệ sách: " + id));
        return convertToResponseDTO(shelf);
    }

    public ShelfResponseDTO createShelf(ShelfRequestDTO requestDTO) {
        Shelf shelf = new Shelf();
        shelf.setName(requestDTO.getName());
        shelf.setFloor(requestDTO.getFloor());

        // Lưu ý: Dùng getCategoryID() (viết hoa ID) cho khớp với file RequestDTO ông vừa gửi
        if (requestDTO.getCategoryID() != null) {
            Category category = categoryRepository.findById(requestDTO.getCategoryID())
                    .orElseThrow(() -> new RuntimeException("Thể loại không tôn tại!"));
            shelf.setCategory(category);
        }
        return convertToResponseDTO(shelfRepository.save(shelf));
    }

    public ShelfResponseDTO updateShelf(Integer id, ShelfRequestDTO requestDTO) {
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kệ sách:: " + id));
        shelf.setName(requestDTO.getName());
        shelf.setFloor(requestDTO.getFloor());

        // Cập nhật luôn cả thể loại khi sửa kệ
        if (requestDTO.getCategoryID() != null) {
            Category category = categoryRepository.findById(requestDTO.getCategoryID())
                    .orElseThrow(() -> new RuntimeException("Thể loại không tôn tại!"));
            shelf.setCategory(category);
        }

        return convertToResponseDTO(shelfRepository.save(shelf));
    }

    public void deleteShelf(Integer id) {
        if (!shelfRepository.existsById(id)) {
            throw new RuntimeException("Kệ sách không tồn tại");
        }
        shelfRepository.deleteById(id);
    }


    private ShelfResponseDTO convertToResponseDTO(Shelf shelf) {
        ShelfResponseDTO dto = new ShelfResponseDTO();
        BeanUtils.copyProperties(shelf, dto);

        // Lấy thông tin từ Entity gán sang DTO để trả về Frontend
        if (shelf.getCategory() != null) {
            dto.setCategoryName(shelf.getCategory().getName());
            // Dùng setCategoryId (d viết thường) theo file ResponseDTO ông gửi
            dto.setCategoryID(shelf.getCategory().getId());
        }
        return dto;
    }

    public List<ShelfResponseDTO> search(String name) {
        return shelfRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
}