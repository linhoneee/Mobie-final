package com.example.brandtests.model;

public class DiscountResult {
    private Double discountAmount;
    private String discountType;
    private Double discountedOrderValue;
    private Double discountedShippingCost;

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public Double getDiscountedOrderValue() {
        return discountedOrderValue;
    }

    public void setDiscountedOrderValue(Double discountedOrderValue) {
        this.discountedOrderValue = discountedOrderValue;
    }

    public Double getDiscountedShippingCost() {
        return discountedShippingCost;
    }

    public void setDiscountedShippingCost(Double discountedShippingCost) {
        this.discountedShippingCost = discountedShippingCost;
    }
}
