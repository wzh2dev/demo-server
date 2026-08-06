package com.demo.server.category.web;

import com.demo.server.category.domain.Category;
import com.demo.server.category.domain.CategoryStatus;
import com.demo.server.category.dto.CategoryCreateRequest;
import com.demo.server.category.exception.BizException;
import com.demo.server.category.service.CategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类控制器。
 */
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public Response create(CategoryCreateRequest req) {
        return Response.created(categoryService.create(req));
    }

    public Response get(Long id) {
        return Response.ok(categoryService.get(id));
    }

    public Response tree() {
        List<Category> tree = categoryService.tree();
        return Response.ok(tree);
    }

    public Response children(Long parentId) {
        List<Category> children = categoryService.children(parentId);
        return Response.ok(children);
    }

    public Response list(CategoryStatus status) {
        List<Category> list = categoryService.list(status);
        return Response.ok(list);
    }

    public Response update(Long id, CategoryCreateRequest req) {
        return Response.ok(categoryService.update(id, req));
    }

    public Response delete(Long id) {
        categoryService.delete(id);
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
            case "CONFLICT":
                return 409;
            case "INVALID_ARGUMENT":
                return 400;
            default:
                return 500;
        }
    }
}
