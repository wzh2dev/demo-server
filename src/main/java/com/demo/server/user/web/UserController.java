package com.demo.server.user.web;

import com.demo.server.user.domain.User;
import com.demo.server.user.domain.UserStatus;
import com.demo.server.user.dto.PageResult;
import com.demo.server.user.dto.UserCreateRequest;
import com.demo.server.user.dto.UserUpdateRequest;
import com.demo.server.user.exception.BizException;
import com.demo.server.user.service.UserService;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器（轻量手写实现，无 Spring 依赖）。
 */
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 分页查询用户。
     */
    public Response list(Map<String, String> params) {
        int pageNumber = parseInt(params.get("pageNumber"), 1);
        int pageSize = parseInt(params.get("pageSize"), 20);
        String keyword = params.get("keyword");
        UserStatus status = parseStatus(params.get("status"));
        PageResult<User> result = userService.list(keyword, status, pageNumber, pageSize);
        return Response.ok(result);
    }

    /**
     * 查询用户详情。
     */
    public Response get(Long id) {
        return Response.ok(userService.get(id));
    }

    /**
     * 创建用户。
     */
    public Response create(UserCreateRequest req) {
        User user = userService.create(req);
        return Response.created(user);
    }

    /**
     * 更新用户。
     */
    public Response update(Long id, UserUpdateRequest req) {
        return Response.ok(userService.update(id, req));
    }

    /**
     * 删除用户。
     */
    public Response delete(Long id) {
        userService.delete(id);
        return Response.noContent();
    }

    /**
     * 统一异常处理。
     */
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

    private int parseInt(String value, int defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private UserStatus parseStatus(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return UserStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
