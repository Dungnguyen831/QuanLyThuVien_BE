package com.library.server.service;

import com.library.server.dto.request.WishlistRequestDTO;
import com.library.server.dto.response.WishlistResponseDTO;
import com.library.server.entity.Wishlist;
import com.library.server.entity.WishlistId;
import com.library.server.entity.Book;
import com.library.server.entity.User;
import com.library.server.repository.WishlistRepository;
import com.library.server.repository.BookRepository;
import com.library.server.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public WishlistService(WishlistRepository wishlistRepository,
                          UserRepository userRepository,
                          BookRepository bookRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    // Helper method: Convert Wishlist Entity to WishlistResponseDTO
    private WishlistResponseDTO mapToDTO(Wishlist wishlist) {
        return WishlistResponseDTO.builder()
                .userId(wishlist.getId().getUserId())
                .bookId(wishlist.getId().getBookId())
                .createdAt(wishlist.getCreatedAt())
                .updatedAt(wishlist.getUpdatedAt())
                .build();
    }

    // Add book to wishlist
    public WishlistResponseDTO addToWishlist(WishlistRequestDTO requestDTO) {
        // Validate user exists
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + requestDTO.getUserId()));

        // Validate book exists
        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + requestDTO.getBookId()));

        // Create composite key
        WishlistId wishlistId = new WishlistId(requestDTO.getUserId(), requestDTO.getBookId());

        // Check if already in wishlist (prevent duplicates)
        if (wishlistRepository.existsById(wishlistId)) {
            throw new RuntimeException("Sách này đã có trong danh sách yêu thích của bạn");
        }

        // Create and save wishlist entry
        Wishlist wishlist = Wishlist.builder()
                .id(wishlistId)
                .user(user)
                .book(book)
                .build();

        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        return mapToDTO(savedWishlist);
    }

    // Get user's wishlist
    public List<WishlistResponseDTO> getUserWishlist(Integer userId) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        return wishlists.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ✅ REMOVED: getBookWishlist(Integer bookId) - No endpoint for this, FE doesn't use

    // Remove from wishlist
    public void removeFromWishlist(WishlistRequestDTO requestDTO) {
        WishlistId wishlistId = new WishlistId(requestDTO.getUserId(), requestDTO.getBookId());

        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mục này trong danh sách yêu thích"));

        wishlistRepository.delete(wishlist);
    }
}

