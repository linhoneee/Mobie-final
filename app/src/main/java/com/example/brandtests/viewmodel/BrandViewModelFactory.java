package com.example.brandtests.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.brandtests.service.BrandService;

public class BrandViewModelFactory implements ViewModelProvider.Factory {
    private final BrandService brandService;

    public BrandViewModelFactory(BrandService brandService) {
        this.brandService = brandService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BrandViewModel.class)) {
            return (T) new BrandViewModel(brandService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
