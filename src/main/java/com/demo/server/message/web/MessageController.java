package com.demo.server.message.web;

import com.demo.server.message.domain.Message;
import com.demo.server.message.dto.MessageCreateRequest;
import com.demo.server.message.dto.PageResult;
import com.demo.server.message.exception.BizException;
import com.demo.server.message.service.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 站内信控制器。
 */
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    public Response send(MessageCreateRequest req) {
        return Response.created(messageService.send(req));
    }

    public Response broadcast(List<Long> userIds, MessageCreateRequest req) {
        List<Message> messages = messageService.broadcast(userIds, req);
        Map<String, Object> body = new HashMap<>();
        body.put("count", messages.size());
        body.put("items", messages);
        return Response.ok(body);
    }

    public Response get(Long id) {
        return Response.ok(messageService.get(id));
    }

    public Response markRead(Long id) {
        return Response.ok(messageService.markRead(id));
    }

    public Response markAllRead(Long userId) {
        int n = messageService.markAllRead(userId);
        Map<String, Object> body = new HashMap<>();
        body.put("updated", n);
        return Response.ok(body);
    }

    public Response listByUser(Long userId, boolean unreadOnly, int pageNumber, int pageSize) {
        PageResult<Message> result = messageService.listByUser(userId, unreadOnly, pageNumber, pageSize);
        return Response.ok(result);
    }

    public Response countUnread(Long userId) {
        long n = messageService.countUnread(userId);
        Map<String, Object> body = new HashMap<>();
        body.put("unread", n);
        return Response.ok(body);
    }

    public Response delete(Long id) {
        messageService.delete(id);
        return Response.noContent();
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
