package com.demo.server.search.domain;

/**
 * 搜索结果项。
 */
public class SearchItem {
    private SearchType type;
    private Long id;
    private String title;
    private String snippet;
    private double score;

    public SearchItem() {
    }

    public SearchItem(SearchType type, Long id, String title, String snippet, double score) {
        this.type = type;
        this.id = id;
        this.title = title;
        this.snippet = snippet;
        this.score = score;
    }

    public SearchType getType() {
        return type;
    }

    public void setType(SearchType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
