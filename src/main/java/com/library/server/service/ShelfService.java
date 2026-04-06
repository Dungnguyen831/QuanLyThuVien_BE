package com.library.server.service; // Khai báo package (Sửa lỗi Missing package statement)

// Import các thư viện từ Spring và Lombok (Sửa lỗi Cannot resolve symbol 'Service', 'RequiredArgsConstructor', ...)
import com.library.server.dto.request.ShelfRequestDTO;
import com.library.server.dto.response.ShelfResponseDTO;
import com.library.server.entity.Shelf;
import com.library.server.repository.ShelfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service // Đánh dấu đây là một Service
@RequiredArgsConstructor // Tự động tạo Constructor để Inject ShelfRepository
public class ShelfService {

    private final ShelfRepository shelfRepository;

    public List<ShelfResponseDTO> getAllShelves() {
        // Sửa lỗi findAll(), Collectors, ShelfResponseDTO
        return shelfRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public ShelfResponseDTO getShelfById(Integer id) {
        // Sửa lỗi findById
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay ke sach: " + id));
        return convertToResponseDTO(shelf);
    }

    public ShelfResponseDTO createShelf(ShelfRequestDTO requestDTO) {
        // Sửa lỗi ShelfRequestDTO, setName, getName...
        Shelf shelf = new Shelf();
        shelf.setName(requestDTO.getName());
        shelf.setFloor(requestDTO.getFloor());
        return convertToResponseDTO(shelfRepository.save(shelf));
    }

    public ShelfResponseDTO updateShelf(Integer id, ShelfRequestDTO requestDTO) {
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay ke sach: " + id));
        shelf.setName(requestDTO.getName());
        shelf.setFloor(requestDTO.getFloor());
        return convertToResponseDTO(shelfRepository.save(shelf));
    }

    public void deleteShelf(Integer id) {
        // Sửa lỗi existsById, deleteById
        if (!shelfRepository.existsById(id)) {
            throw new RuntimeException("Ke sach khong ton tai");
        }
        shelfRepository.deleteById(id);
    }

    private ShelfResponseDTO convertToResponseDTO(Shelf shelf) {
        // Sửa lỗi BeanUtils
        ShelfResponseDTO dto = new ShelfResponseDTO();
        BeanUtils.copyProperties(shelf, dto);
        return dto;
    }
    public List<ShelfResponseDTO> search(String name) {
        return shelfRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
}
