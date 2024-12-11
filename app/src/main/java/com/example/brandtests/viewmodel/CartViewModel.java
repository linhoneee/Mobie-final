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
                    addItemResult.setValue("thêm sản phẩm thành công .");
                    Log.d(TAG, "Thêm sản phẩm thành công: " + response.body());
                } else {
                    addItemResult.setValue("thêm sản phẩm thất bại.");
                    Log.e(TAG, "Failed to add item to cart: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                addItemResult.setValue("lỗi khi thêm: " + t.getMessage());
                Log.e(TAG, "Error adding item to cart", t);
            }
        });
    }

    public void removeItemFromCart(Long userId, Item item) {
        cartService.removeItemFromCart(userId, item).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    addItemResult.setValue("Giảm sản phẩm thành công.");
                    Log.d(TAG, "Item reduced in cart successfully: " + response.body());
                } else {
                    addItemResult.setValue("giảm sản phẩm thất bại.");
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
                    Log.e(TAG, "không thể tải giỏ hàng: " + response.code() + " " + response.message());
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

        // Log incoming data
        Log.d(TAG, "Selected Items: ");
        for (Item item : selectedItems) {
            Log.d(TAG, "Item: " + item.getName() + ", ProductId: " + item.getProductId());
        }

        Log.d(TAG, "Item-Warehouse ID Map: ");
        for (Map.Entry<Long, String> entry : itemWarehouseIdsMap.entrySet()) {
            Log.d(TAG, "ProductId: " + entry.getKey() + " -> WarehouseIds: " + entry.getValue());
        }

        // Process and group items by warehouse
        for (Item item : selectedItems) {
            String warehouseIds = itemWarehouseIdsMap.get(item.getProductId());
            if (warehouseIds != null) {
                // Log warehouse IDs for each item
                Log.d(TAG, "Item: " + item.getName() + " has Warehouse IDs: " + warehouseIds);

                String[] warehouseIdArray = warehouseIds.split(",");
                for (String warehouseId : warehouseIdArray) {
                    groupedItems.putIfAbsent(warehouseId, new ArrayList<>()); //putIfAbsent là kiểm tra nếu không có thì tạo mới key với value này trong array
                    groupedItems.get(warehouseId).add(item);

                    // Log item being added to a warehouse
                    Log.d(TAG, "Adding item to Warehouse: " + warehouseId);
                }
            } else {
                Log.e(TAG, "Warehouse IDs are null for item: " + item.getName());
            }
        }

        // Log grouped items
        Log.d(TAG, "Grouped Items by Warehouse: ");
        for (Map.Entry<String, List<Item>> entry : groupedItems.entrySet()) {
            Log.d(TAG, "Warehouse: " + entry.getKey() + " contains " + entry.getValue().size() + " items.");
            for (Item item : entry.getValue()) {
                Log.d(TAG, "  Item: " + item.getName() + ", ProductId: " + item.getProductId());
            }
        }

        // Set grouped items to warehouseGroups
        warehouseGroups.setValue(groupedItems);
        Log.d(TAG, "Grouped items set to warehouseGroups.");
    }


    // Thêm phương thức clearCart
    public void clearProductInCart(Long userId, Item item) {
        cartService.removeItemFromCart(userId, item).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    // Xử lý thành công
                    fetchCart(userId);  // Lấy lại giỏ hàng
                } else {
                    // Xử lý thất bại
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                // Xử lý khi lỗi
            }
        });
    }


}
