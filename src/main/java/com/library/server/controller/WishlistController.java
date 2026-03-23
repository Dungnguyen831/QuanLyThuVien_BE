package com.library.server.controller;

import com.library.server.dto.request.WishlistRequestDTO;
import com.library.server.dto.response.WishlistResponseDTO;
import com.library.server.entity.Book;
import com.library.server.entity.Wishlist;
import com.library.server.service.BookService;
import com.library.server.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;
    private final BookService bookService;

    public WishlistController(WishlistService wishlistService, BookService bookService) {
        this.wishlistService = wishlistService;
        this.bookService = bookService;
    }

    // POST /api/v1/wishlists - Add book to wishlist
    @PostMapping
    public ResponseEntity<?> addToWishlist(@RequestBody WishlistRequestDTO requestDTO) {
        try {
            WishlistResponseDTO wishlist = wishlistService.addToWishlist(requestDTO);
            return ResponseEntity.ok(wishlist);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/v1/wishlists/user/{userId} - Get user's wishlist with book details
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserWishlist(@PathVariable Integer userId) {
        try {
            List<WishlistResponseDTO> wishlists = wishlistService.getUserWishlist(userId);
            
            // ✅ Map sang Book objects với details
            List<Book> books = wishlists.stream()
                .map(w -> bookService.getBookById(w.getBookId()))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(books);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/v1/wishlists/book/{bookId} - Get users who wishlisted this book
    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getBookWishlist(@PathVariable Integer bookId) {
        try {
            List<WishlistResponseDTO> wishlists = wishlistService.getBookWishlist(bookId);
            return ResponseEntity.ok(wishlists);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/v1/wishlists - Remove from wishlist
    @DeleteMapping
    public ResponseEntity<?> removeFromWishlist(@RequestBody WishlistRequestDTO requestDTO) {
        try {
            wishlistService.removeFromWishlist(requestDTO);
            return ResponseEntity.ok("Đã xóa khỏi danh sách yêu thích");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

