package com.demo.server.review.repository;

import com.demo.server.review.domain.Review;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 评论仓储，内存实现。
 */
public class ReviewRepository {

    private final Map<Long, Review> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(0L);

    public Review save(Review review) {
        if (review.getId() == null) {
            review.setId(idSeq.incrementAndGet());
            long now = System.currentTimeMillis();
            review.setCreatedAt(now);
            review.setUpdatedAt(now);
        } else {
            review.setUpdatedAt(System.currentTimeMillis());
        }
        store.put(review.getId(), review);
        return review;
    }

    public Review findById(Long id) {
        if (id == null) {
            return null;
        }
        return store.get(id);
    }

    public List<Review> findByProduct(Long productId, Integer rating,
                                      int pageNumber, int pageSize) {
        return store.values().stream()
                .filter(r -> !r.isHidden())
                .filter(r -> productId == null || productId.equals(r.getProductId()))
                .filter(r -> rating == null || rating == r.getRating())
                .sorted(Comparator.comparing(Review::getCreatedAt,
                        Comparator.nullsFirst(Comparator.reverseOrder())))
                .skip((long) (pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public long countByProduct(Long productId, Integer rating) {
        return store.values().stream()
                .filter(r -> !r.isHidden())
                .filter(r -> productId == null || productId.equals(r.getProductId()))
                .filter(r -> rating == null || rating == r.getRating())
                .count();
    }

    public List<Review> findByUser(Long userId) {
        if (userId == null) {
            return java.util.Collections.emptyList();
        }
        return store.values().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .collect(Collectors.toList());
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    /**
     * 计算商品评分汇总。
     */
    public RatingSummary summaryByProduct(Long productId) {
        RatingSummary summary = new RatingSummary();
        store.values().stream()
                .filter(r -> !r.isHidden())
                .filter(r -> productId != null && productId.equals(r.getProductId()))
                .forEach(r -> {
                    summary.total++;
                    summary.sum += r.getRating();
                    switch (r.getRating()) {
                        case 5:
                            summary.five++;
                            break;
                        case 4:
                            summary.four++;
                            break;
                        case 3:
                            summary.three++;
                            break;
                        case 2:
                            summary.two++;
                            break;
                        case 1:
                            summary.one++;
                            break;
                        default:
                            break;
                    }
                });
        return summary;
    }

    /**
     * 评分汇总。
     */
    public static class RatingSummary {
        public long total;
        public long sum;
        public long five;
        public long four;
        public long three;
        public long two;
        public long one;

        public double average() {
            return total == 0 ? 0d : (double) sum / total;
        }
    }
}
