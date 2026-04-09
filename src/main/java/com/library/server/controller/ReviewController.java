package com.library.server.controller;

import com.library.server.dto.request.ReviewRequestDTO;
import com.library.server.dto.response.ReviewResponseDTO;
import com.library.server.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> createReview(@RequestBody ReviewRequestDTO requestDTO) {
        try {
            ReviewResponseDTO review = reviewService.createReview(requestDTO);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/v1/reviews - Get all reviews
    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        List<ReviewResponseDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    // GET /api/v1/reviews/{id} - Get review by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable Integer id) {
        try {
            ReviewResponseDTO review = reviewService.getReviewById(id);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByBookId(@PathVariable Integer bookId) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }

    // PUT /api/v1/reviews/{id} - Update review
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Integer id,
                                          @RequestBody ReviewRequestDTO requestDTO) {
        try {
            ReviewResponseDTO review = reviewService.updateReview(id, requestDTO);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/v1/reviews/{id} - Delete review
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Integer id) {
        try {
            reviewService.deleteReview(id);
            // ✅ Trả về JSON response
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đánh giá đã được xóa thành công",
                    "id", id
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}

