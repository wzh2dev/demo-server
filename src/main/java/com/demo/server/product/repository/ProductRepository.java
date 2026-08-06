package com.demo.server.product.repository;

import com.demo.server.product.domain.Product;
import com.demo.server.product.domain.ProductStatus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 商品仓储，内存实现。
 */
public class ProductRepository {

    private final Map<Long, Product> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(0L);

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idSeq.incrementAndGet());
            long now = System.currentTimeMillis();
            product.setCreatedAt(now);
            product.setUpdatedAt(now);
        } else {
            product.setUpdatedAt(System.currentTimeMillis());
        }
        store.put(product.getId(), product);
        return product;
    }

    public Product findById(Long id) {
        if (id == null) {
            return null;
        }
        return store.get(id);
    }

    public List<Product> findAll(Long categoryId, Double minPrice, Double maxPrice,
                                 String sort, int pageNumber, int pageSize) {
        return store.values().stream()
                .filter(p -> categoryId == null || categoryId.equals(p.getCategoryId()))
                .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                .sorted((a, b) -> compareBySort(a, b, sort))
                .skip((long) (pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public long count(Long categoryId, Double minPrice, Double maxPrice) {
        return store.values().stream()
                .filter(p -> categoryId == null || categoryId.equals(p.getCategoryId()))
                .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                .count();
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    public List<Product> findByStatus(ProductStatus status) {
        return store.values().stream()
                .filter(p -> p.getStatus() == status)
                .collect(Collectors.toList());
    }

    private int compareBySort(Product a, Product b, String sort) {
        if (sort == null) {
            return 0;
        }
        switch (sort) {
            case "price_asc":
                return Double.compare(a.getPrice(), b.getPrice());
            case "price_desc":
                return Double.compare(b.getPrice(), a.getPrice());
            case "created_desc":
                return Long.compare(b.getCreatedAt() == null ? 0L : b.getCreatedAt(),
                        a.getCreatedAt() == null ? 0L : a.getCreatedAt());
            case "sold_desc":
                return Integer.compare(b.getSales(), a.getSales());
            default:
                return 0;
        }
    }
}
