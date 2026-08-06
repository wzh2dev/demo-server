package com.demo.server.message.dto;

import com.demo.server.message.domain.MessageType;

/**
 * 站内信创建请求。
 */
public class MessageCreateRequest {
    private Long userId;
    private String title;
    private String content;
    private MessageType type;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
