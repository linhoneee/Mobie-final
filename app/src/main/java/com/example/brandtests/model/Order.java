package com.example.brandtests.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Order {
    private int id;
    private int userId;
    private String items;  // Giữ chuỗi JSON thay vì List<OrderItem>
    private String selectedShipping;  // Giữ chuỗi JSON thay vì Shipping
    private String distanceData;  // Giữ chuỗi JSON thay vì DistanceData
    private double total;
    private String createdAt;

    public Order(int id, int userId, String items, String selectedShipping, String distanceData, double total, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.selectedShipping = selectedShipping;
        this.distanceData = distanceData;
        this.total = total;
        this.createdAt = createdAt;
    }

    // Thay đổi tên phương thức để tránh trùng lặp
    public List<OrderItem> decodeItems() {
        return new Gson().fromJson(items, new TypeToken<List<OrderItem>>() {}.getType());
    }

    public Shipping decodeSelectedShipping() {
        return new Gson().fromJson(selectedShipping, Shipping.class);
    }

    public DistanceData decodeDistanceData() {
        return new Gson().fromJson(distanceData, DistanceData.class);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
