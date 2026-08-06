package com.demo.server.message.service;

import com.demo.server.message.domain.Message;
import com.demo.server.message.domain.MessageType;
import com.demo.server.message.dto.MessageCreateRequest;
import com.demo.server.message.dto.PageResult;
import com.demo.server.message.exception.NotFoundException;
import com.demo.server.message.exception.ValidationException;
import com.demo.server.message.repository.MessageRepository;

import java.util.List;
import java.util.Objects;

/**
 * 站内信领域服务。
 */
public class MessageService {

    private static final int TITLE_MAX_LEN = 128;
    private static final int CONTENT_MAX_LEN = 2000;

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = Objects.requireNonNull(messageRepository);
    }

    /**
     * 发送站内信。
     */
    public Message send(MessageCreateRequest req) {
        validateCreate(req);
        Message message = new Message();
        message.setUserId(req.getUserId());
        message.setTitle(req.getTitle());
        message.setContent(req.getContent());
        message.setType(req.getType() == null ? MessageType.SYSTEM : req.getType());
        message.setRead(false);
        return messageRepository.save(message);
    }

    /**
     * 广播站内信。
     */
    public List<Message> broadcast(List<Long> userIds, MessageCreateRequest req) {
        if (userIds == null || userIds.isEmpty()) {
            throw new ValidationException("userIds is empty");
        }
        validateCreate(req);
        Message template = new Message();
        template.setTitle(req.getTitle());
        template.setContent(req.getContent());
        template.setType(req.getType() == null ? MessageType.SYSTEM : req.getType());
        return messageRepository.broadcast(userIds, template);
    }

    public Message get(Long id) {
        Message message = messageRepository.findById(id);
        if (message == null) {
            throw new NotFoundException("message", id);
        }
        return message;
    }

    /**
     * 标记已读。
     */
    public Message markRead(Long id) {
        Message message = get(id);
        if (!message.isRead()) {
            message.setRead(true);
            message.setReadAt(System.currentTimeMillis());
            messageRepository.save(message);
        }
        return message;
    }

    /**
     * 标记某用户全部已读。
     */
    public int markAllRead(Long userId) {
        if (userId == null) {
            throw new ValidationException("userId is null");
        }
        int n = 0;
        // 简单遍历，实际应批量更新
        for (Message m : messageRepository.findByUser(userId, true, 1, Integer.MAX_VALUE)) {
            m.setRead(true);
            m.setReadAt(System.currentTimeMillis());
            messageRepository.save(m);
            n++;
        }
        return n;
    }

    public PageResult<Message> listByUser(Long userId, boolean unreadOnly,
                                          int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 20;
        }
        List<Message> items = messageRepository.findByUser(userId, unreadOnly, pageNumber, pageSize);
        long total = messageRepository.countByUser(userId, unreadOnly);
        return new PageResult<>(items, total, pageNumber, pageSize);
    }

    public long countUnread(Long userId) {
        return messageRepository.countUnread(userId);
    }

    public void delete(Long id) {
        if (!messageRepository.deleteById(id)) {
            throw new NotFoundException("message", id);
        }
    }

    private void validateCreate(MessageCreateRequest req) {
        if (req == null) {
            throw new ValidationException("request is null");
        }
        if (req.getUserId() == null) {
            throw new ValidationException("userId is null");
        }
        if (req.getTitle() == null || req.getTitle().isEmpty()) {
            throw new ValidationException("title is empty");
        }
        if (req.getTitle().length() > TITLE_MAX_LEN) {
            throw new ValidationException("title too long");
        }
        if (req.getContent() == null || req.getContent().isEmpty()) {
            throw new ValidationException("content is empty");
        }
        if (req.getContent().length() > CONTENT_MAX_LEN) {
            throw new ValidationException("content too long");
        }
    }
}
