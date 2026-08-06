package com.demo.server.favorite.dto;

import com.demo.server.favorite.domain.FavoriteType;

/**
 * 收藏创建请求。
 */
public class FavoriteCreateRequest {
    private FavoriteType type;
    private Long targetId;

    public FavoriteType getType() {
        return type;
    }

    public void setType(FavoriteType type) {
        this.type = type;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
}
