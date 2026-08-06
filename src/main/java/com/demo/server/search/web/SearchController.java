package com.demo.server.search.web;

import com.demo.server.search.domain.SearchItem;
import com.demo.server.search.domain.SearchType;
import com.demo.server.search.dto.SearchResult;
import com.demo.server.search.exception.BizException;
import com.demo.server.search.service.SearchService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索控制器。
 */
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    public Response search(String query, SearchType type, int pageNumber, int pageSize) {
        SearchResult result = searchService.search(query, type, pageNumber, pageSize);
        return Response.ok(result);
    }

    public Response suggest(String prefix, int limit) {
        List<String> suggestions = searchService.suggest(prefix, limit);
        Map<String, Object> body = new HashMap<>();
        body.put("items", suggestions);
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
            case "INVALID_ARGUMENT":
                return 400;
            default:
                return 500;
        }
    }

    @SuppressWarnings("unused")
    private SearchType parseType(String value) {
        if (value == null || value.isEmpty()) {
            return SearchType.ALL;
        }
        try {
            return SearchType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SearchType.ALL;
        }
    }

    @SuppressWarnings("unused")
    private List<SearchItem> emptyList() {
        return java.util.Collections.emptyList();
    }
}
