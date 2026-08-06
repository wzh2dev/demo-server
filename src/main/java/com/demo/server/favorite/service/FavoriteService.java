package com.demo.server.favorite.service;

import com.demo.server.favorite.domain.Favorite;
import com.demo.server.favorite.domain.FavoriteType;
import com.demo.server.favorite.dto.FavoriteCreateRequest;
import com.demo.server.favorite.exception.ConflictException;
import com.demo.server.favorite.exception.NotFoundException;
import com.demo.server.favorite.exception.ValidationException;
import com.demo.server.favorite.repository.FavoriteRepository;

import java.util.List;
import java.util.Objects;

/**
 * 收藏领域服务。
 */
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = Objects.requireNonNull(favoriteRepository);
    }

    public Favorite add(Long userId, FavoriteCreateRequest req) {
        if (userId == null) {
            throw new ValidationException("userId is null");
        }
        if (req == null) {
            throw new ValidationException("request is null");
        }
        if (req.getType() == null) {
            throw new ValidationException("type is null");
        }
        if (req.getTargetId() == null) {
            throw new ValidationException("targetId is null");
        }
        if (favoriteRepository.findByUserAndTarget(userId, req.getType(), req.getTargetId()) != null) {
            throw new ConflictException("favorite already exists");
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setType(req.getType());
        favorite.setTargetId(req.getTargetId());
        return favoriteRepository.save(favorite);
    }

    public void remove(Long userId, FavoriteType type, Long targetId) {
        if (!favoriteRepository.deleteByUserAndTarget(userId, type, targetId)) {
            throw new NotFoundException("favorite", targetId);
        }
    }

    public void removeById(Long id) {
        if (!favoriteRepository.deleteById(id)) {
            throw new NotFoundException("favorite", id);
        }
    }

    public List<Favorite> list(Long userId, FavoriteType type, int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 20;
        }
        return favoriteRepository.findByUser(userId, type, pageNumber, pageSize);
    }

    public long count(Long userId, FavoriteType type) {
        return favoriteRepository.countByUser(userId, type);
    }

    public boolean isFavorite(Long userId, FavoriteType type, Long targetId) {
        return favoriteRepository.findByUserAndTarget(userId, type, targetId) != null;
    }
}
