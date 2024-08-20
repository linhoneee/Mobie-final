package com.example.brandtests.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

public class Cart {
    private Long id;
    private Long userId;
    private String items; // Giữ nguyên dạng chuỗi
    private BigDecimal total;

    // Hàm để chuyển đổi chuỗi JSON items thành List<Item>
    public List<Item> getItemsList() {
        Type listType = new TypeToken<List<Item>>() {}.getType();
        return new Gson().fromJson(this.items, listType);
    }


    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
