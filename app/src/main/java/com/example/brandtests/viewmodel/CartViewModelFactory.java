package com.example.brandtests.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.brandtests.service.CartService;

public class CartViewModelFactory implements ViewModelProvider.Factory {
    private final CartService cartService;

    public CartViewModelFactory(CartService cartService) {
        this.cartService = cartService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CartViewModel.class)) {
            return (T) new CartViewModel(cartService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
