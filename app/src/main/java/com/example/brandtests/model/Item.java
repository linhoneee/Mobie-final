package com.example.brandtests.model;

import java.math.BigDecimal;

public class Item {
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private Integer weight;
    private String primaryImageUrl;

    // Constructor, Getters, and Setters
    public Item(Long productId, String name, BigDecimal price, Integer quantity, Integer weight, String primaryImageUrl) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.weight = weight;
        this.primaryImageUrl = primaryImageUrl;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }
}
