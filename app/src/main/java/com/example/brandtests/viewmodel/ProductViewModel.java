package com.example.brandtests.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.brandtests.model.ProductDTOuser;
import com.example.brandtests.service.ProductService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductViewModel extends ViewModel {

    private static final String TAG = "ProductViewModel";
    private MutableLiveData<List<ProductDTOuser>> products;
    private ProductService productService;

    public ProductViewModel(ProductService productService) {
        this.productService = productService;
        products = new MutableLiveData<>();
        Log.d(TAG, "ProductViewModel: Initialized");
        loadProducts();
    }

    public LiveData<List<ProductDTOuser>> getProducts() {
        return products;
    }

    private void loadProducts() {
        Log.d(TAG, "loadProducts: Loading products from API");
        productService.getAllProductsByUser().enqueue(new Callback<List<ProductDTOuser>>() {
            @Override
            public void onResponse(Call<List<ProductDTOuser>> call, Response<List<ProductDTOuser>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: Successfully loaded products with size " + response.body().size());
                    products.setValue(response.body());
                } else {
                    Log.e(TAG, "onResponse: Failed to load products, response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ProductDTOuser>> call, Throwable t) {
                Log.e(TAG, "onFailure: Error loading products", t);
            }
        });
    }
}
