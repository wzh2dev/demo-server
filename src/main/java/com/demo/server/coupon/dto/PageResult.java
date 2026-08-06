package com.demo.server.coupon.dto;

import java.util.List;

/**
 * 分页结果。
 */
public class PageResult<T> {
    private List<T> items;
    private long total;
    private int pageNumber;
    private int pageSize;

    public PageResult() {
    }

    public PageResult(List<T> items, long total, int pageNumber, int pageSize) {
        this.items = items;
        this.total = total;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
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
}
