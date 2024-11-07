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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartViewModel extends ViewModel {
    private static final String TAG = "CartViewModel";
    private CartService cartService;
    private InventoryService inventoryService;
    private MutableLiveData<String> addItemResult = new MutableLiveData<>();
    private MutableLiveData<Cart> cart = new MutableLiveData<>();
    private MutableLiveData<List<Inventory>> inventories = new MutableLiveData<>();
    private MutableLiveData<Map<String, List<Item>>> warehouseGroups = new MutableLiveData<>();

    // Constructor cho CartService và InventoryService
    public CartViewModel(CartService cartService, InventoryService inventoryService) {
        this.cartService = cartService;
        this.inventoryService = inventoryService;
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

    public LiveData<Map<String, List<Item>>> getWarehouseGroups() {
        return warehouseGroups;
    }

    public void addItemToCart(Long userId, Item item) {
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
        cartService.removeItemFromCart(userId, item).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    addItemResult.setValue("Item reduced in cart successfully.");
                    Log.d(TAG, "Item reduced in cart successfully: " + response.body());
                } else {
                    addItemResult.setValue("Failed to reduce item in cart.");
                    Log.e(TAG, "Failed to reduce item in cart: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                addItemResult.setValue("Error reducing item in cart: " + t.getMessage());
                Log.e(TAG, "Error reducing item in cart", t);
            }
        });
    }

    public void fetchCart(Long userId) {
        cartService.getCart(userId).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
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
        inventoryService.getAllInventories().enqueue(new Callback<List<Inventory>>() {
            @Override
            public void onResponse(Call<List<Inventory>> call, Response<List<Inventory>> response) {
                if (response.isSuccessful() && response.body() != null) {
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

    // Hàm xử lý cập nhật số lượng sản phẩm
    public void updateItemQuantity(Long userId, Item item, int newQuantity) {
        int difference = newQuantity - item.getQuantity();
        if (difference > 0) {
            Item newItem = new Item(
                    item.getProductId(),
                    item.getName(),
                    item.getPrice(),
                    difference,
                    item.getWeight(),
                    item.getPrimaryImageUrl()
            );
            addItemToCart(userId, newItem);
        } else if (difference < 0) {
            Item newItem = new Item(
                    item.getProductId(),
                    item.getName(),
                    item.getPrice(),
                    Math.abs(difference),
                    item.getWeight(),
                    item.getPrimaryImageUrl()
            );
            removeItemFromCart(userId, newItem);
        }
        item.setQuantity(newQuantity); // Cập nhật số lượng mới cho item
    }

    // Hàm phân nhóm các sản phẩm theo warehouseId
    public void groupItemsByWarehouse(List<Item> selectedItems, Map<Long, String> itemWarehouseIdsMap) {
        Map<String, List<Item>> groupedItems = new HashMap<>();

        for (Item item : selectedItems) {
            String warehouseIds = itemWarehouseIdsMap.get(item.getProductId());
            if (warehouseIds != null) {
                String[] warehouseIdArray = warehouseIds.split(",");
                for (String warehouseId : warehouseIdArray) {
                    groupedItems.putIfAbsent(warehouseId, new ArrayList<>());
                    groupedItems.get(warehouseId).add(item);
                }
            } else {
                Log.e(TAG, "Warehouse IDs is null for item: " + item.getName());
            }
        }

        warehouseGroups.setValue(groupedItems);
    }
}
