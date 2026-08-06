package com.demo.server.user.repository;

import com.demo.server.user.domain.User;
import com.demo.server.user.domain.UserStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 用户仓储，内存实现，仅用于演示。
 */
public class UserRepository {

    private final Map<Long, User> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(0L);

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idSeq.incrementAndGet());
            long now = System.currentTimeMillis();
            user.setCreatedAt(now);
            user.setUpdatedAt(now);
        } else {
            user.setUpdatedAt(System.currentTimeMillis());
        }
        store.put(user.getId(), user);
        return user;
    }

    public User findById(Long id) {
        if (id == null) {
            return null;
        }
        return store.get(id);
    }

    public User findByMobile(String mobile) {
        if (mobile == null) {
            return null;
        }
        for (User u : store.values()) {
            if (mobile.equals(u.getMobile())) {
                return u;
            }
        }
        return null;
    }

    public List<User> findAll(String keyword, UserStatus status, int pageNumber, int pageSize) {
        return store.values().stream()
                .filter(u -> status == null || u.getStatus() == status)
                .filter(u -> keyword == null || keyword.isEmpty()
                        || (u.getNickname() != null && u.getNickname().contains(keyword))
                        || (u.getMobile() != null && u.getMobile().contains(keyword)))
                .skip((long) (pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public long count(String keyword, UserStatus status) {
        return store.values().stream()
                .filter(u -> status == null || u.getStatus() == status)
                .filter(u -> keyword == null || keyword.isEmpty()
                        || (u.getNickname() != null && u.getNickname().contains(keyword))
                        || (u.getMobile() != null && u.getMobile().contains(keyword)))
                .count();
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    public List<User> snapshot() {
        return new ArrayList<>(store.values());
    }
}
