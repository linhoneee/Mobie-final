package com.example.brandtests.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.brandtests.model.Cart;
import com.example.brandtests.model.Inventory;
import com.example.brandtests.model.Item;
import com.example.brandtests.service.CartService;
import com.example.brandtests.service.InventoryService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import android.util.Log;



public class CartViewModel extends ViewModel {
    private static final String TAG = "CartViewModel";
    private CartService cartService;
    private InventoryService inventoryService;
    private MutableLiveData<String> addItemResult = new MutableLiveData<>();
    private MutableLiveData<Cart> cart = new MutableLiveData<>();
    private MutableLiveData<List<Inventory>> inventories = new MutableLiveData<>();

    // Constructor cho CartService và InventoryService
    public CartViewModel(CartService cartService, InventoryService inventoryService) {
        this.cartService = cartService;
        this.inventoryService = inventoryService;
    }

    // Constructor chỉ cho CartService
    public CartViewModel(CartService cartService) {
        this.cartService = cartService;
    }

    // Getter cho inventories
    public LiveData<List<Inventory>> getInventories() {
        return inventories;
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

    public void removeItemFromCart(Long userId, Item item) {
        Log.d(TAG, "addItemToCart: Sending request to add item to cart for userId: " + userId);
        cartService.removeItemFromCart(userId, item).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    addItemResult.setValue("Item reduce to cart successfully.");
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

    public void fetchInventories() {
        Log.d(TAG, "fetchInventories: Sending request to fetch all inventories");
        inventoryService.getAllInventories().enqueue(new Callback<List<Inventory>>() {
            @Override
            public void onResponse(Call<List<Inventory>> call, Response<List<Inventory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "fetchInventories: Inventories fetched successfully: " + response.body());
                    inventories.setValue(response.body());
                } else {
                    Log.e(TAG, "Failed to fetch inventories: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Inventory>> call, Throwable t) {
                Log.e(TAG, "Error fetching inventories", t);
            }
        });
    }
}
