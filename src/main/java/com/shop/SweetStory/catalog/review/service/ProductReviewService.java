package com.shop.SweetStory.catalog.review.service;

import com.shop.SweetStory.auth.entity.User;
import com.shop.SweetStory.auth.repository.UserRepository;
import com.shop.SweetStory.catalog.generalDto.ActionMessageResponse;
import com.shop.SweetStory.catalog.product.entity.Product;
import com.shop.SweetStory.catalog.product.repository.ProductRepository;
import com.shop.SweetStory.catalog.review.dto.CreateReviewRequest;
import com.shop.SweetStory.catalog.review.dto.ReviewListResponse;
import com.shop.SweetStory.catalog.review.dto.ReviewResponse;
import com.shop.SweetStory.catalog.review.dto.UpdateReviewRequest;
import com.shop.SweetStory.catalog.review.entity.ProductReview;
import com.shop.SweetStory.catalog.review.repository.ProductReviewRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductReviewService(
            ProductReviewRepository reviewRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public ReviewListResponse getReviewsByProductId(Long productId) {
        findProductOrThrow(productId);
        List<ProductReview> reviews = reviewRepository.findAllByProductIdOrderByCreatedAtDesc(productId);
        List<ReviewResponse> items = reviews.stream()
                .map(this::toResponse)
                .toList();

        int count = reviews.size();
        BigDecimal averageRating = calculateAverageRating(reviews);
        return new ReviewListResponse(items, averageRating, count);
    }

    @Transactional
    public ReviewResponse createReview(Long productId, CreateReviewRequest request) {
        Product product = findProductOrThrow(productId);
        User currentUser = resolveCurrentUser();

        if (reviewRepository.existsByProductIdAndUserId(productId, currentUser.getId())) {
            throw new IllegalStateException("Вы уже оставили отзыв на этот товар");
        }

        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setUser(currentUser);
        review.setRating(request.rating());
        review.setComment(request.comment() != null ? request.comment().trim() : null);

        ProductReview saved = reviewRepository.save(review);
        return toResponse(saved);
    }

    @Transactional
    public ReviewResponse updateReview(Long productId, Long reviewId, UpdateReviewRequest request) {
        findProductOrThrow(productId);
        User currentUser = resolveCurrentUser();

        ProductReview review = reviewRepository.findByIdAndProductId(reviewId, productId)
                .orElseThrow(() -> new NoSuchElementException("Review not found with id: " + reviewId));

        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Нельзя редактировать чужой отзыв");
        }

        if (request.rating() != null) {
            review.setRating(request.rating());
        }
        if (request.comment() != null) {
            review.setComment(request.comment().trim());
        }

        ProductReview saved = reviewRepository.save(review);
        return toResponse(saved);
    }

    @Transactional
    public ActionMessageResponse deleteReview(Long productId, Long reviewId) {
        findProductOrThrow(productId);
        User currentUser = resolveCurrentUser();

        ProductReview review = reviewRepository.findByIdAndProductId(reviewId, productId)
                .orElseThrow(() -> new NoSuchElementException("Review not found with id: " + reviewId));

        boolean isOwner = review.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = Boolean.TRUE.equals(currentUser.getIsAdmin());
        if (!isOwner && !isAdmin) {
            throw new SecurityException("Нельзя удалить чужой отзыв");
        }

        reviewRepository.delete(review);
        return new ActionMessageResponse("success", "Отзыв удален");
    }

    @Transactional
    public ActionMessageResponse deleteReviewById(Long reviewId) {
        User currentUser = resolveCurrentUser();
        boolean isAdmin = Boolean.TRUE.equals(currentUser.getIsAdmin());
        if (!isAdmin) {
            throw new SecurityException("Доступ разрешён только администратору");
        }

        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("Review not found with id: " + reviewId));

        reviewRepository.delete(review);
        return new ActionMessageResponse("success", "Отзыв удален");
    }

    private BigDecimal calculateAverageRating(List<ProductReview> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return null;
        }
        int sum = reviews.stream().mapToInt(ProductReview::getRating).sum();
        return BigDecimal.valueOf(sum)
                .divide(BigDecimal.valueOf(reviews.size()), 2, RoundingMode.HALF_UP);
    }

    private Product findProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + productId));
    }

    private User resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || authentication.getName() == null) {
            throw new IllegalStateException("Authenticated user not found in security context");
        }
        String principalName = authentication.getName();
        return userRepository.findByEmail(principalName)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + principalName));
    }

    private ReviewResponse toResponse(ProductReview review) {
        String authorName = review.getUser() != null ? review.getUser().getName() : null;
        return new ReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                authorName,
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}