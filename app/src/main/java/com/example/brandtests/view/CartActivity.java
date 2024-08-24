package com.example.brandtests.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.brandtests.R;
import com.example.brandtests.adapter.CartAdapter;
import com.example.brandtests.model.Item;
import com.example.brandtests.service.CartRetrofitClient;
import com.example.brandtests.service.InventoryRetrofitClient;
import com.example.brandtests.viewmodel.CartViewModel;
import com.example.brandtests.viewmodel.CheckoutViewModel;
import com.example.brandtests.viewmodel.CartWithInventoryViewModelFactory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private CartViewModel cartViewModel;
    private CheckoutViewModel checkoutViewModel;
    private Long userId;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ListView cartListView = findViewById(R.id.cartListView);
        Button checkoutButton = findViewById(R.id.checkoutButton);

        userId = getIntent().getLongExtra("userId", -1);

        cartViewModel = new ViewModelProvider(this,
                new CartWithInventoryViewModelFactory(CartRetrofitClient.getCartService(), InventoryRetrofitClient.getInventoryService()))
                .get(CartViewModel.class);

        checkoutViewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);

        if (userId != -1) {
            cartViewModel.fetchCart(userId);
            cartViewModel.fetchInventories();

            cartViewModel.getCart().observe(this, cart -> {
                if (cart != null) {
                    cartViewModel.getInventories().observe(this, inventories -> {
                        if (inventories != null) {
                            List<Item> items = cart.getItemsList();
                            adapter = new CartAdapter(this, items, userId, inventories);
                            cartListView.setAdapter(adapter);
                        }
                    });
                } else {
                    Toast.makeText(CartActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
                }
            });

            checkoutButton.setOnClickListener(v -> {
                List<Item> selectedItems = adapter.getSelectedItems();

                if (selectedItems.isEmpty()) {
                    Toast.makeText(CartActivity.this, "No items selected", Toast.LENGTH_SHORT).show();
                } else {
                    showCheckoutDialog(selectedItems);
                }
            });
        } else {
            Toast.makeText(CartActivity.this, "Invalid user ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCheckoutDialog(List<Item> selectedItems) {
        Map<Long, String> itemWarehouseIdsMap = adapter.getItemWarehouseIdsMap();

        // Gọi phương thức groupItemsByWarehouse từ ViewModel
        checkoutViewModel.groupItemsByWarehouse(selectedItems, itemWarehouseIdsMap);

        checkoutViewModel.getWarehouseGroups().observe(this, warehouseGroups -> {
            if (warehouseGroups == null) return;

            // Inflate layout cho dialog
            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.dialog_checkout, null);

            LinearLayout productsContainer = dialogView.findViewById(R.id.dialogProductsContainer);

            // Add từng nhóm warehouse vào container
            for (Map.Entry<String, List<Item>> entry : warehouseGroups.entrySet()) {
                String warehouseId = entry.getKey();
                List<Item> itemsInGroup = entry.getValue();

                // Tạo layout cho từng nhóm warehouse
                View groupView = inflater.inflate(R.layout.warehouse_group_item, null);
                TextView warehouseTitle = groupView.findViewById(R.id.warehouseTitle);
                LinearLayout groupItemsContainer = groupView.findViewById(R.id.groupItemsContainer);

                warehouseTitle.setText("Warehouse ID: " + warehouseId);

                for (Item item : itemsInGroup) {
                    View productView = inflater.inflate(R.layout.checkout_item, null);

                    ImageView productImage = productView.findViewById(R.id.checkoutProductImage);
                    TextView productName = productView.findViewById(R.id.checkoutProductName);
                    TextView productPrice = productView.findViewById(R.id.checkoutProductPrice);
                    TextView productQuantity = productView.findViewById(R.id.checkoutProductQuantity);

                    productName.setText(item.getName());
                    productPrice.setText("Price: $" + item.getPrice().toString());
                    productQuantity.setText("Quantity: " + item.getQuantity());

                    if (item.getPrimaryImageUrl() != null && !item.getPrimaryImageUrl().isEmpty()) {
                        String fullImageUrl = "http://10.0.2.2:6001" + item.getPrimaryImageUrl();
                        Picasso.get().load(fullImageUrl).into(productImage);
                    } else {
                        String defaultImageUrl = "https://via.placeholder.com/150";
                        Picasso.get().load(defaultImageUrl).into(productImage);
                    }

                    groupItemsContainer.addView(productView);
                }

                // Thêm sự kiện click vào toàn bộ nhóm để chọn
                groupView.setOnClickListener(v -> {
                    checkoutViewModel.setSelectedWarehouseId(warehouseId);

                    Intent intent = new Intent(CartActivity.this, CheckoutPage.class);
                    intent.putExtra("selectedWarehouseId", warehouseId);
                    intent.putParcelableArrayListExtra("selectedGroupItems", new ArrayList<>(itemsInGroup));
                    startActivity(intent);
                });

                // Thêm nhóm vào container chính
                productsContainer.addView(groupView);
            }

            new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}
