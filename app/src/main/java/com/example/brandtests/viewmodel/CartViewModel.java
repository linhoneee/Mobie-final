package com.example.brandtests.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.brandtests.model.Cart;
import com.example.brandtests.model.Item;
import com.example.brandtests.service.CartService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartViewModel extends ViewModel {
    private static final String TAG = "CartViewModel";
    private CartService cartService;
    private MutableLiveData<String> addItemResult = new MutableLiveData<>();
    private MutableLiveData<Cart> cart = new MutableLiveData<>();

    public CartViewModel(CartService cartService) {
        this.cartService = cartService;
    }

    public LiveData<String> getAddItemResult() {
        return addItemResult;
    }

    public LiveData<Cart> getCart() {
        return cart;
    }

    public void addItemToCart(Long userId, Item item) {
        Log.d(TAG, "addItemToCart: Sending request to add item to cart for userId: " + userId);
        cartService.addItemToCart(userId, item).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    addItemResult.setValue("Item added to cart successfully.");
                    Log.d(TAG, "Item added to cart successfully: " + response.body());
                } else {
                    addItemResult.setValue("Failed to add item to cart.");
                    Log.e(TAG, "Failed to add item to cart: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                addItemResult.setValue("Error adding item to cart: " + t.getMessage());
                Log.e(TAG, "Error adding item to cart", t);
            }
        });
    }

    public void fetchCart(Long userId) {
        Log.d(TAG, "fetchCart: Sending request to fetch cart for userId: " + userId);
        cartService.getCart(userId).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "fetchCart: Cart fetched successfully: " + response.body());
                    cart.setValue(response.body());
                } else {
                    Log.e(TAG, "Failed to fetch cart: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e(TAG, "Error fetching cart", t);
            }
        });
    }
}
