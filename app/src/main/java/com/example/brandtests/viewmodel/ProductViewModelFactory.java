package com.example.brandtests.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.brandtests.service.ProductService;

public class ProductViewModelFactory implements ViewModelProvider.Factory {

    private final ProductService productService;

    public ProductViewModelFactory(ProductService productService) {
        this.productService = productService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProductViewModel.class)) {
            return (T) new ProductViewModel(productService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
