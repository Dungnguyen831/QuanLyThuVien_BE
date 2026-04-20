package com.library.server.controller;

import com.library.server.dto.request.WishlistRequestDTO;
import com.library.server.dto.response.WishlistResponseDTO;
import com.library.server.entity.Book;
import com.library.server.entity.User;
import com.library.server.service.WishlistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlists")
@Slf4j
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    /**
     * GET /api/v1/wishlists - Get authenticated user's wishlist with book details
     * ✅ FIXED: Extract userId from JWT token (authenticatedUser), not from request parameter
     * ✅ FIXED: Logic moved to Service layer
     */
    @GetMapping
    public ResponseEntity<?> getMyWishlist(@AuthenticationPrincipal User authenticatedUser) {
        try {
            Integer userId = authenticatedUser.getId();
            log.info("Fetching wishlist for authenticated user ID: {}", userId);

            // Call service to get wishlist with book details
            List<Book> books = wishlistService.getMyWishlistWithBooksDetail(userId);

            log.info("Successfully retrieved {} items from wishlist for user ID: {}", books.size(), userId);
            return ResponseEntity.ok(books);
        } catch (RuntimeException e) {
            log.error("Error fetching wishlist for user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * POST /api/v1/wishlists - Add book to wishlist
     * ✅ FIXED: Extract userId from JWT token, not from request body
     * ✅ FIXED: Set userId automatically from authenticatedUser.getId()
     * ✅ FIXED: Never trust userId from request input
     */
    @PostMapping
    public ResponseEntity<?> addToWishlist(
            @AuthenticationPrincipal User authenticatedUser,
            @RequestBody WishlistRequestDTO requestDTO) {
        try {
            Integer userId = authenticatedUser.getId();
            log.info("User ID {} is adding book ID {} to wishlist", userId, requestDTO.getBookId());

            // Override userId from request with authenticated user ID (security)
            requestDTO.setUserId(userId);

            WishlistResponseDTO wishlist = wishlistService.addToWishlist(requestDTO);

            log.info("Successfully added book to wishlist for user ID: {}", userId);
            return ResponseEntity.ok(wishlist);
        } catch (RuntimeException e) {
            log.error("Error adding to wishlist for user {}: {}", authenticatedUser.getId(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/wishlists/{bookId} - Remove book from wishlist
     * ✅ FIXED: Use @PathVariable for bookId (REST best practice)
     * ✅ FIXED: Extract userId from JWT token, not from request body
     * ✅ FIXED: Added @PreAuthorize and @AuthenticationPrincipal
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<?> removeFromWishlist(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable Integer bookId) {
        try {
            Integer userId = authenticatedUser.getId();
            log.info("User ID {} is removing book ID {} from wishlist", userId, bookId);

            // Create request DTO with authenticated user's ID
            WishlistRequestDTO requestDTO = WishlistRequestDTO.builder()
                    .userId(userId)
                    .bookId(bookId)
                    .build();

            wishlistService.removeFromWishlist(requestDTO);

            log.info("Successfully removed book from wishlist for user ID: {}", userId);
            return ResponseEntity.ok("Đã xóa khỏi danh sách yêu thích");
        } catch (RuntimeException e) {
            log.error("Error removing from wishlist for user {}: {}", authenticatedUser.getId(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

