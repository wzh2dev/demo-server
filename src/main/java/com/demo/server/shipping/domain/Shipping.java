package com.demo.server.shipping.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 物流单实体。
 */
public class Shipping {
    private Long id;
    private Long orderId;
    private String company;
    private String trackingNo;
    private ShippingStatus status = ShippingStatus.PENDING;
    private List<Track> tracks = new ArrayList<>();
    private Long shippedAt;
    private Long deliveredAt;
    private Long createdAt;
    private Long updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public ShippingStatus getStatus() {
        return status;
    }

    public void setStatus(ShippingStatus status) {
        this.status = status;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks == null ? new ArrayList<>() : tracks;
    }

    public Long getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(Long shippedAt) {
        this.shippedAt = shippedAt;
    }

    public Long getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Long deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
