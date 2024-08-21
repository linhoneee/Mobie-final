package com.example.brandtests.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.brandtests.service.CartService;
import com.example.brandtests.service.InventoryService;

public class CartWithInventoryViewModelFactory implements ViewModelProvider.Factory {

    private final CartService cartService;
    private final InventoryService inventoryService;

    public CartWithInventoryViewModelFactory(CartService cartService, InventoryService inventoryService) {
        this.cartService = cartService;
        this.inventoryService = inventoryService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CartViewModel.class)) {
            return (T) new CartViewModel(cartService, inventoryService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
