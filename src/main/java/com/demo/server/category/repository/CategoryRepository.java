package com.demo.server.category.repository;

import com.demo.server.category.domain.Category;
import com.demo.server.category.domain.CategoryStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 分类仓储，内存实现。
 */
public class CategoryRepository {

    private final Map<Long, Category> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(0L);

    public Category save(Category category) {
        if (category.getId() == null) {
            category.setId(idSeq.incrementAndGet());
            long now = System.currentTimeMillis();
            category.setCreatedAt(now);
            category.setUpdatedAt(now);
        } else {
            category.setUpdatedAt(System.currentTimeMillis());
        }
        store.put(category.getId(), category);
        return category;
    }

    public Category findById(Long id) {
        if (id == null) {
            return null;
        }
        return store.get(id);
    }

    public List<Category> findAll(CategoryStatus status) {
        return store.values().stream()
                .filter(c -> status == null || status == c.getStatus())
                .sorted(Comparator
                        .comparing(Category::getSortOrder)
                        .thenComparing(Category::getId))
                .collect(Collectors.toList());
    }

    public List<Category> findByParent(Long parentId) {
        return store.values().stream()
                .filter(c -> parentId == null
                        ? c.getParentId() == null
                        : parentId.equals(c.getParentId()))
                .sorted(Comparator
                        .comparing(Category::getSortOrder)
                        .thenComparing(Category::getId))
                .collect(Collectors.toList());
    }

    public List<Category> findChildren(Long id) {
        if (id == null) {
            return new ArrayList<>();
        }
        return store.values().stream()
                .filter(c -> id.equals(c.getParentId()))
                .collect(Collectors.toList());
    }

    public long countChildren(Long id) {
        if (id == null) {
            return 0L;
        }
        return store.values().stream()
                .filter(c -> id.equals(c.getParentId()))
                .count();
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }
}
