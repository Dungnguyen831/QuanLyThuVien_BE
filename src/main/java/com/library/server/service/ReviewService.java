package com.library.server.service;

import com.library.server.dto.request.ReviewRequestDTO;
import com.library.server.dto.response.ReviewResponseDTO;
import com.library.server.entity.Review;
import com.library.server.entity.Book;
import com.library.server.entity.User;
import com.library.server.repository.ReviewRepository;
import com.library.server.repository.BookRepository;
import com.library.server.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

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
                .fullName(review.getUser() != null ? review.getUser().getFullName() : null)  // ✅ NEW: Get fullName from User
                .bookId(review.getBook() != null ? review.getBook().getId() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    // ✅ FIXED: Create a new review with authenticated user ID
    public ReviewResponseDTO createReview(Integer authenticatedUserId, ReviewRequestDTO requestDTO) {
        // ✅ SECURITY: Use authenticated user ID, not from request body
        if (authenticatedUserId == null || authenticatedUserId <= 0) {
            logger.warn("Invalid authenticated user ID: {}", authenticatedUserId);
            throw new IllegalArgumentException("User ID không hợp lệ");
        }

        logger.info("User {} creating review for bookId: {}", authenticatedUserId, requestDTO.getBookId());

        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> {
                    logger.warn("Authenticated user not found: {}", authenticatedUserId);
                    return new RuntimeException("Không tìm thấy người dùng với ID: " + authenticatedUserId);
                });

        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> {
                    logger.warn("Book not found: {}", requestDTO.getBookId());
                    return new RuntimeException("Không tìm thấy sách với ID: " + requestDTO.getBookId());
                });

        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(requestDTO.getRating())
                .comment(requestDTO.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);
        logger.info("Review created successfully for user {}: ID {}", authenticatedUserId, savedReview.getId());
        return mapToDTO(savedReview);
    }

    // ✅ REMOVED: getAllReviews() - FE doesn't use this endpoint

    // ✅ REMOVED: getReviewById(Integer id) - FE doesn't use this endpoint

    // ✅ FIXED: Update review with ownership verification
    public ReviewResponseDTO updateReview(Integer id, Integer authenticatedUserId, ReviewRequestDTO requestDTO) {
        // ✅ SECURITY: Validate ID and user ID
        if (id == null || id <= 0) {
            logger.warn("Invalid review ID for update: {}", id);
            throw new IllegalArgumentException("ID không hợp lệ");
        }

        if (authenticatedUserId == null || authenticatedUserId <= 0) {
            logger.warn("Invalid authenticated user ID for update: {}", authenticatedUserId);
            throw new IllegalArgumentException("User ID không hợp lệ");
        }

        logger.info("User {} updating review ID: {}", authenticatedUserId, id);

        // ✅ CRITICAL: Use findByIdAndUserId to verify OWNERSHIP
        Review review = reviewRepository.findByIdAndUserId(id, authenticatedUserId)
                .orElseThrow(() -> {
                    logger.warn("User {} attempted to update review {} that they don't own", authenticatedUserId, id);
                    return new IllegalArgumentException("Đánh giá không tồn tại hoặc không phải của bạn");
                });

        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> {
                    logger.warn("Book not found for update: {}", requestDTO.getBookId());
                    return new IllegalArgumentException("Không tìm thấy sách");
                });

        review.setBook(book);
        review.setRating(requestDTO.getRating());
        review.setComment(requestDTO.getComment());

        Review updatedReview = reviewRepository.save(review);
        logger.info("User {} successfully updated review {}", authenticatedUserId, id);
        return mapToDTO(updatedReview);
    }

    // ✅ FIXED: Delete review with ownership verification
    public void deleteReview(Integer id, Integer authenticatedUserId) {
        // ✅ SECURITY: Validate ID and user ID
        if (id == null || id <= 0) {
            logger.warn("Invalid review ID for deletion: {}", id);
            throw new IllegalArgumentException("ID không hợp lệ");
        }

        if (authenticatedUserId == null || authenticatedUserId <= 0) {
            logger.warn("Invalid authenticated user ID for deletion: {}", authenticatedUserId);
            throw new IllegalArgumentException("User ID không hợp lệ");
        }

        logger.info("User {} attempting to delete review ID: {}", authenticatedUserId, id);

        // ✅ CRITICAL: Use findByIdAndUserId to verify OWNERSHIP before deletion
        Review review = reviewRepository.findByIdAndUserId(id, authenticatedUserId)
                .orElseThrow(() -> {
                    logger.warn("User {} attempted to delete review {} that they don't own", authenticatedUserId, id);
                    return new IllegalArgumentException("Đánh giá không tồn tại hoặc không phải của bạn");
                });

        reviewRepository.delete(review);
        logger.info("User {} successfully deleted review: {}", authenticatedUserId, id);
    }

    // Get reviews by book ID (Cách 2: Custom Query)
    public List<ReviewResponseDTO> getReviewsByBookId(Integer bookId) {
        // Kiểm tra sách có tồn tại hay không
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + bookId));

        // Gọi custom query từ Repository
        List<Review> reviews = reviewRepository.findByBookId(bookId);

        // Convert Entity sang DTO
        return reviews.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
