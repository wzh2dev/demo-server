package com.demo.server.review.web;

import com.demo.server.review.domain.Review;
import com.demo.server.review.dto.PageResult;
import com.demo.server.review.dto.ReviewCreateRequest;
import com.demo.server.review.exception.BizException;
import com.demo.server.review.repository.ReviewRepository;
import com.demo.server.review.service.ReviewService;

import java.util.HashMap;
import java.util.Map;

/**
 * 评论控制器。
 */
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public Response create(Long userId, ReviewCreateRequest req) {
        return Response.created(reviewService.create(userId, req));
    }

    public Response get(Long id) {
        return Response.ok(reviewService.get(id));
    }

    public Response delete(Long id) {
        reviewService.delete(id);
        return Response.noContent();
    }

    public Response hide(Long id) {
        return Response.ok(reviewService.hide(id));
    }

    public Response show(Long id) {
        return Response.ok(reviewService.show(id));
    }

    public Response listByProduct(Long productId, Integer rating, int pageNumber, int pageSize) {
        PageResult<Review> result = reviewService.listByProduct(productId, rating, pageNumber, pageSize);
        return Response.ok(result);
    }

    public Response listByUser(Long userId) {
        return Response.ok(reviewService.listByUser(userId));
    }

    public Response summary(Long productId) {
        ReviewRepository.RatingSummary summary = reviewService.summary(productId);
        Map<String, Object> body = new HashMap<>();
        body.put("total", summary.total);
        body.put("average", summary.average());
        body.put("five", summary.five);
        body.put("four", summary.four);
        body.put("three", summary.three);
        body.put("two", summary.two);
        body.put("one", summary.one);
        return Response.ok(body);
    }

    public Response handleError(BizException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", ex.getCode());
        body.put("message", ex.getMessage());
        int status = mapStatus(ex.getCode());
        return new Response(status, body);
    }

    private int mapStatus(String code) {
        switch (code) {
            case "NOT_FOUND":
                return 404;
            case "INVALID_ARGUMENT":
                return 400;
            default:
                return 500;
        }
    }
}
