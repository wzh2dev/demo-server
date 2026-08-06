package com.demo.server.search.dto;

import com.demo.server.search.domain.SearchItem;

import java.util.List;

/**
 * 搜索结果。
 */
public class SearchResult {
    private String query;
    private long total;
    private List<SearchItem> items;
    private int pageNumber;
    private int pageSize;
    private long tookMs;

    public SearchResult() {
    }

    public SearchResult(String query, long total, List<SearchItem> items,
                        int pageNumber, int pageSize, long tookMs) {
        this.query = query;
        this.total = total;
        this.items = items;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.tookMs = tookMs;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<SearchItem> getItems() {
        return items;
    }

    public void setItems(List<SearchItem> items) {
        this.items = items;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTookMs() {
        return tookMs;
    }

    public void setTookMs(long tookMs) {
        this.tookMs = tookMs;
    }
}
