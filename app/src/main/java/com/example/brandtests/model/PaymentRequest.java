package com.example.brandtests.model;

import java.util.List;

public class PaymentRequest {
    private Long id;
    private Integer userId;
    private List<Item> items;
    private Shipping selectedShipping;
    private DistanceData distanceData;
    private Double total;
    private String platform; // Thêm tham số platform để xác định nền tảng

    // Constructors
    public PaymentRequest(Long id, Integer userId, List<Item> items, DistanceData distanceData, Double total, String platform) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.distanceData = distanceData;
        this.total = total;
        this.selectedShipping = new Shipping();  // Gán một đối tượng trống
        this.platform = platform; // Gán giá trị platform
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Shipping getSelectedShipping() {
        return selectedShipping;
    }

    public void setSelectedShipping(Shipping selectedShipping) {
        this.selectedShipping = selectedShipping;
    }

    public DistanceData getDistanceData() {
        return distanceData;
    }

    public void setDistanceData(DistanceData distanceData) {
        this.distanceData = distanceData;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
