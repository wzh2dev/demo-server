package com.demo.server.message.repository;

import com.demo.server.message.domain.Message;
import com.demo.server.message.domain.MessageType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 站内信仓储，内存实现。
 */
public class MessageRepository {

    private final Map<Long, Message> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(0L);

    public Message save(Message message) {
        if (message.getId() == null) {
            message.setId(idSeq.incrementAndGet());
            message.setCreatedAt(System.currentTimeMillis());
        }
        store.put(message.getId(), message);
        return message;
    }

    public Message findById(Long id) {
        if (id == null) {
            return null;
        }
        return store.get(id);
    }

    public List<Message> findByUser(Long userId, boolean unreadOnly,
                                    int pageNumber, int pageSize) {
        if (userId == null) {
            return java.util.Collections.emptyList();
        }
        return store.values().stream()
                .filter(m -> userId.equals(m.getUserId()))
                .filter(m -> !unreadOnly || !m.isRead())
                .sorted(Comparator.comparing(Message::getCreatedAt,
                        Comparator.nullsFirst(Comparator.reverseOrder())))
                .skip((long) (pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public long countByUser(Long userId, boolean unreadOnly) {
        if (userId == null) {
            return 0L;
        }
        return store.values().stream()
                .filter(m -> userId.equals(m.getUserId()))
                .filter(m -> !unreadOnly || !m.isRead())
                .count();
    }

    public long countUnread(Long userId) {
        if (userId == null) {
            return 0L;
        }
        return store.values().stream()
                .filter(m -> userId.equals(m.getUserId()))
                .filter(m -> !m.isRead())
                .count();
    }

    public List<Message> findByType(MessageType type, int pageNumber, int pageSize) {
        if (type == null) {
            return java.util.Collections.emptyList();
        }
        return store.values().stream()
                .filter(m -> type == m.getType())
                .sorted(Comparator.comparing(Message::getCreatedAt,
                        Comparator.nullsFirst(Comparator.reverseOrder())))
                .skip((long) (pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    /**
     * 广播给多个用户。
     */
    public List<Message> broadcast(List<Long> userIds, Message template) {
        java.util.List<Message> created = new java.util.ArrayList<>();
        if (userIds == null || template == null) {
            return created;
        }
        for (Long uid : userIds) {
            if (uid == null) {
                continue;
            }
            Message m = new Message();
            m.setUserId(uid);
            m.setTitle(template.getTitle());
            m.setContent(template.getContent());
            m.setType(template.getType() == null ? MessageType.SYSTEM : template.getType());
            m.setRead(false);
            created.add(save(m));
        }
        return created;
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }
}
