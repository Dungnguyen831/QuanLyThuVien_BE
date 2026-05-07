package com.library.server.controller;

import com.library.server.dto.request.ReviewRequestDTO;
import com.library.server.dto.response.ReviewResponseDTO;
import com.library.server.entity.User;
import com.library.server.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // POST /api/v1/reviews - Create a new review
    @PostMapping
    public ResponseEntity<?> createReview(
            @RequestBody ReviewRequestDTO requestDTO,
            @AuthenticationPrincipal User authenticatedUser) {
        try {
            // ✅ SECURITY: Use authenticated user ID, not from request
            ReviewResponseDTO review = reviewService.createReview(authenticatedUser.getId(), requestDTO);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/v1/reviews/book/{bookId} - Get reviews by book ID (must be before GET /{id})
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByBookId(@PathVariable Integer bookId) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }

    // ✅ REMOVED: GET /api/v1/reviews - getAllReviews (FE doesn't use)
    // ✅ REMOVED: GET /api/v1/reviews/{id} - getReviewById (FE doesn't use)

    // PUT /api/v1/reviews/{id} - Update review
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable Integer id,
            @RequestBody ReviewRequestDTO requestDTO,
            @AuthenticationPrincipal User authenticatedUser) {
        try {
            // ✅ SECURITY: Verify ownership - user can only update their own review
            ReviewResponseDTO review = reviewService.updateReview(id, authenticatedUser.getId(), requestDTO);
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // DELETE /api/v1/reviews/{id} - Delete review
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Integer id,
            @AuthenticationPrincipal User authenticatedUser) {
        try {
            // ✅ SECURITY: Verify ownership - user can only delete their own review
            reviewService.deleteReview(id, authenticatedUser.getId());
            // ✅ Return JSON response
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đánh giá đã được xóa thành công",
                    "id", id
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}

