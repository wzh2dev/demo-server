package com.demo.server.favorite.repository;

import com.demo.server.favorite.domain.Favorite;
import com.demo.server.favorite.domain.FavoriteType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 收藏仓储，内存实现。
 */
public class FavoriteRepository {

    private final Map<Long, Favorite> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(0L);

    public Favorite save(Favorite favorite) {
        if (favorite.getId() == null) {
            favorite.setId(idSeq.incrementAndGet());
            favorite.setCreatedAt(System.currentTimeMillis());
        }
        store.put(favorite.getId(), favorite);
        return favorite;
    }

    public Favorite findById(Long id) {
        if (id == null) {
            return null;
        }
        return store.get(id);
    }

    public Favorite findByUserAndTarget(Long userId, FavoriteType type, Long targetId) {
        if (userId == null || type == null || targetId == null) {
            return null;
        }
        return store.values().stream()
                .filter(f -> userId.equals(f.getUserId()))
                .filter(f -> type == f.getType())
                .filter(f -> targetId.equals(f.getTargetId()))
                .findFirst()
                .orElse(null);
    }

    public List<Favorite> findByUser(Long userId, FavoriteType type, int pageNumber, int pageSize) {
        if (userId == null) {
            return java.util.Collections.emptyList();
        }
        return store.values().stream()
                .filter(f -> userId.equals(f.getUserId()))
                .filter(f -> type == null || type == f.getType())
                .sorted(Comparator.comparing(Favorite::getCreatedAt,
                        Comparator.nullsFirst(Comparator.reverseOrder())))
                .skip((long) (pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public long countByUser(Long userId, FavoriteType type) {
        if (userId == null) {
            return 0L;
        }
        return store.values().stream()
                .filter(f -> userId.equals(f.getUserId()))
                .filter(f -> type == null || type == f.getType())
                .count();
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    public boolean deleteByUserAndTarget(Long userId, FavoriteType type, Long targetId) {
        Favorite favorite = findByUserAndTarget(userId, type, targetId);
        if (favorite == null) {
            return false;
        }
        return store.remove(favorite.getId()) != null;
    }
}
