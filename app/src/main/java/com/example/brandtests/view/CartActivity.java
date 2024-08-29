package com.example.brandtests.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ListView cartListView = findViewById(R.id.cartListView);
        Button checkoutButton = findViewById(R.id.checkoutButton);

        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong("UserID", -1);
        Log.d("CartActivity", "Retrieved UserID from SharedPreferences: " + userId);

        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cartViewModel = new ViewModelProvider(this,
                new CartWithInventoryViewModelFactory(CartRetrofitClient.getCartService(), InventoryRetrofitClient.getInventoryService()))
                .get(CartViewModel.class);

        checkoutViewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);

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
    }

    private void showCheckoutDialog(List<Item> selectedItems) {
        Map<Long, String> itemWarehouseIdsMap = adapter.getItemWarehouseIdsMap();
        checkoutViewModel.groupItemsByWarehouse(selectedItems, itemWarehouseIdsMap);

        checkoutViewModel.getWarehouseGroups().observe(this, warehouseGroups -> {
            if (warehouseGroups == null) return;

            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.dialog_checkout, null);
            LinearLayout productsContainer = dialogView.findViewById(R.id.dialogProductsContainer);

            for (Map.Entry<String, List<Item>> entry : warehouseGroups.entrySet()) {
                String warehouseId = entry.getKey();
                List<Item> itemsInGroup = entry.getValue();

                View groupView = inflater.inflate(R.layout.warehouse_group_item, null);
                TextView warehouseTitle = groupView.findViewById(R.id.warehouseTitle);
                warehouseTitle.setText("Warehouse ID: " + warehouseId);

                LinearLayout groupItemsContainer = groupView.findViewById(R.id.groupItemsContainer);
                for (Item item : itemsInGroup) {
                    View productView = inflater.inflate(R.layout.checkout_item, null);
                    ImageView productImage = productView.findViewById(R.id.checkoutProductImage);
                    TextView productName = productView.findViewById(R.id.checkoutProductName);
                    productName.setText(item.getName());
                    TextView productPrice = productView.findViewById(R.id.checkoutProductPrice);
                    productPrice.setText("Price: $" + item.getPrice().toString());
                    TextView productQuantity = productView.findViewById(R.id.checkoutProductQuantity);
                    productQuantity.setText("Quantity: " + item.getQuantity());

                    if (item.getPrimaryImageUrl() != null && !item.getPrimaryImageUrl().isEmpty()) {
                        Picasso.get().load("http://10.0.2.2:6001" + item.getPrimaryImageUrl()).into(productImage);
                    } else {
                        Picasso.get().load("https://via.placeholder.com/150").into(productImage);
                    }

                    groupItemsContainer.addView(productView);
                }

                groupView.setOnClickListener(v -> {
                    Intent intent = new Intent(CartActivity.this, CheckoutPage.class);
                    intent.putExtra("selectedWarehouseId", warehouseId);
                    intent.putParcelableArrayListExtra("selectedGroupItems", new ArrayList<>(itemsInGroup));
                    startActivity(intent);
                });

                productsContainer.addView(groupView);
            }

            new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}
