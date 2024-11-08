package com.example.brandtests.model;

public class OrderItem {
    public int productId;
    public String name;
    public double price;
    public int quantity;
    public double weight;
    public String primaryImageUrl;
    public boolean isReviewed;

    public OrderItem(int productId, String name, double price, int quantity, double weight, String primaryImageUrl, boolean isReviewed) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.weight = weight;
        this.primaryImageUrl = primaryImageUrl;
        this.isReviewed = isReviewed;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }

    public boolean isReviewed() {
        return isReviewed;
    }

    public void setReviewed(boolean reviewed) {
        isReviewed = reviewed;
    }
}
