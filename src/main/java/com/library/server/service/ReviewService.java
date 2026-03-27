package com.library.server.service;

import com.library.server.dto.request.ReviewRequestDTO;
import com.library.server.dto.response.ReviewResponseDTO;
import com.library.server.entity.Review;
import com.library.server.entity.Book;
import com.library.server.entity.User;
import com.library.server.repository.ReviewRepository;
import com.library.server.repository.BookRepository;
import com.library.server.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public ReviewService(ReviewRepository reviewRepository,
                        UserRepository userRepository,
                        BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    // Helper method: Convert Review Entity to ReviewResponseDTO
    private ReviewResponseDTO mapToDTO(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .bookId(review.getBook() != null ? review.getBook().getId() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    // Create a new review
    public ReviewResponseDTO createReview(ReviewRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + requestDTO.getUserId()));

        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + requestDTO.getBookId()));

        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(requestDTO.getRating())
                .comment(requestDTO.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);
        return mapToDTO(savedReview);
    }

    // Get all reviews
    public List<ReviewResponseDTO> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get review by ID
    public ReviewResponseDTO getReviewById(Integer id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá với ID: " + id));
        return mapToDTO(review);
    }

    // Update review
    public ReviewResponseDTO updateReview(Integer id, ReviewRequestDTO requestDTO) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá với ID: " + id));

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + requestDTO.getUserId()));

        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + requestDTO.getBookId()));

        review.setUser(user);
        review.setBook(book);
        review.setRating(requestDTO.getRating());
        review.setComment(requestDTO.getComment());

        Review updatedReview = reviewRepository.save(review);
        return mapToDTO(updatedReview);
    }

    // Delete review
    public void deleteReview(Integer id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá với ID: " + id));
        reviewRepository.delete(review);
    }
}
