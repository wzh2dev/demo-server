package com.demo.server.favorite.web;

import com.demo.server.favorite.domain.Favorite;
import com.demo.server.favorite.domain.FavoriteType;
import com.demo.server.favorite.dto.FavoriteCreateRequest;
import com.demo.server.favorite.exception.BizException;
import com.demo.server.favorite.service.FavoriteService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收藏控制器。
 */
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    public Response add(Long userId, FavoriteCreateRequest req) {
        return Response.created(favoriteService.add(userId, req));
    }

    public Response remove(Long userId, FavoriteType type, Long targetId) {
        favoriteService.remove(userId, type, targetId);
        return Response.noContent();
    }

    public Response removeById(Long id) {
        favoriteService.removeById(id);
        return Response.noContent();
    }

    public Response list(Long userId, FavoriteType type, int pageNumber, int pageSize) {
        List<Favorite> list = favoriteService.list(userId, type, pageNumber, pageSize);
        return Response.ok(list);
    }

    public Response count(Long userId, FavoriteType type) {
        long n = favoriteService.count(userId, type);
        Map<String, Object> body = new HashMap<>();
        body.put("count", n);
        return Response.ok(body);
    }

    public Response check(Long userId, FavoriteType type, Long targetId) {
        boolean favorite = favoriteService.isFavorite(userId, type, targetId);
        Map<String, Object> body = new HashMap<>();
        body.put("favorite", favorite);
        return Response.ok(body);
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
            case "CONFLICT":
                return 409;
            case "INVALID_ARGUMENT":
                return 400;
            default:
                return 500;
        }
    }
}
