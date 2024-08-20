package com.example.brandtests.model;


public class ProductDTOuser {
    private Product product;
    private ProductImage primaryImage;

    // Getters and Setters
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductImage getPrimaryImage() {
        return primaryImage;
    }

    public void setPrimaryImage(ProductImage primaryImage) {
        this.primaryImage = primaryImage;
    }
}
