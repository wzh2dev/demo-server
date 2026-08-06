package com.demo.server.product.web;

import com.demo.server.product.domain.Product;
import com.demo.server.product.dto.PageResult;
import com.demo.server.product.dto.ProductCreateRequest;
import com.demo.server.product.dto.ProductUpdateRequest;
import com.demo.server.product.exception.BizException;
import com.demo.server.product.service.ProductService;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品控制器。
 */
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public Response list(Map<String, String> params) {
        int pageNumber = parseInt(params.get("pageNumber"), 1);
        int pageSize = parseInt(params.get("pageSize"), 20);
        Long categoryId = parseLong(params.get("categoryId"));
        Double minPrice = parseDouble(params.get("minPrice"));
        Double maxPrice = parseDouble(params.get("maxPrice"));
        String sort = params.get("sort");
        PageResult<Product> result = productService.list(categoryId, minPrice, maxPrice, sort, pageNumber, pageSize);
        return Response.ok(result);
    }

    public Response get(Long id) {
        return Response.ok(productService.get(id));
    }

    public Response create(ProductCreateRequest req) {
        return Response.created(productService.create(req));
    }

    public Response update(Long id, ProductUpdateRequest req) {
        return Response.ok(productService.update(id, req));
    }

    public Response delete(Long id) {
        productService.delete(id);
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

    private Long parseLong(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
