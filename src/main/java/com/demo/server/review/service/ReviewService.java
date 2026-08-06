package com.demo.server.review.service;

import com.demo.server.review.domain.Review;
import com.demo.server.review.dto.PageResult;
import com.demo.server.review.dto.ReviewCreateRequest;
import com.demo.server.review.exception.NotFoundException;
import com.demo.server.review.exception.ValidationException;
import com.demo.server.review.repository.ReviewRepository;

import java.util.List;
import java.util.Objects;

/**
 * 评论领域服务。
 */
public class ReviewService {

    private static final int CONTENT_MAX_LEN = 500;
    private static final int MAX_IMAGES = 9;

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = Objects.requireNonNull(reviewRepository);
    }

    /**
     * 发表评论。
     */
    public Review create(Long userId, ReviewCreateRequest req) {
        if (userId == null) {
            throw new ValidationException("userId is null");
        }
        if (req == null) {
            throw new ValidationException("request is null");
        }
        if (req.getProductId() == null) {
            throw new ValidationException("productId is null");
        }
        if (req.getRating() < 1 || req.getRating() > 5) {
            throw new ValidationException("rating must be 1-5");
        }
        if (req.getContent() == null || req.getContent().isEmpty()) {
            throw new ValidationException("content is empty");
        }
        if (req.getContent().length() > CONTENT_MAX_LEN) {
            throw new ValidationException("content too long");
        }
        if (req.getImages() != null && req.getImages().size() > MAX_IMAGES) {
            throw new ValidationException("too many images, max " + MAX_IMAGES);
        }
        Review review = new Review();
        review.setProductId(req.getProductId());
        review.setUserId(userId);
        review.setRating(req.getRating());
        review.setContent(req.getContent());
        if (req.getImages() != null) {
            review.setImages(req.getImages());
        }
        review.setHidden(false);
        return reviewRepository.save(review);
    }

    public Review get(Long id) {
        Review review = reviewRepository.findById(id);
        if (review == null) {
            throw new NotFoundException("review", id);
        }
        return review;
    }

    public void delete(Long id) {
        if (!reviewRepository.deleteById(id)) {
            throw new NotFoundException("review", id);
        }
    }

    /**
     * 隐藏评论（管理员）。
     */
    public Review hide(Long id) {
        Review review = get(id);
        review.setHidden(true);
        return reviewRepository.save(review);
    }

    /**
     * 显示评论。
     */
    public Review show(Long id) {
        Review review = get(id);
        review.setHidden(false);
        return reviewRepository.save(review);
    }

    public PageResult<Review> listByProduct(Long productId, Integer rating,
                                            int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 20;
        }
        List<Review> items = reviewRepository.findByProduct(productId, rating, pageNumber, pageSize);
        long total = reviewRepository.countByProduct(productId, rating);
        return new PageResult<>(items, total, pageNumber, pageSize);
    }

    public List<Review> listByUser(Long userId) {
        return reviewRepository.findByUser(userId);
    }

    /**
     * 商品评分汇总。
     */
    public ReviewRepository.RatingSummary summary(Long productId) {
        if (productId == null) {
            throw new ValidationException("productId is null");
        }
        return reviewRepository.summaryByProduct(productId);
    }
}
