package com.demo.server.shipping.dto;

/**
 * 轨迹上报请求。
 */
public class TrackCreateRequest {
    private Long time;
    private String location;
    private String description;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
