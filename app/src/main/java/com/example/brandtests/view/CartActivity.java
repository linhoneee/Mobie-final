package com.example.brandtests.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.brandtests.R;
import com.example.brandtests.adapter.CartAdapter;
import com.example.brandtests.model.Inventory;
import com.example.brandtests.model.Item;
import com.example.brandtests.service.CartRetrofitClient;
import com.example.brandtests.service.InventoryRetrofitClient;
import com.example.brandtests.viewmodel.CartViewModel;
import com.example.brandtests.viewmodel.CartWithInventoryViewModelFactory;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private CartViewModel cartViewModel;
    private Long userId;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ListView cartListView = findViewById(R.id.cartListView);
        Button checkoutButton = findViewById(R.id.checkoutButton);

        userId = getIntent().getLongExtra("userId", -1);

        // Sử dụng CartWithInventoryViewModelFactory để khởi tạo CartViewModel
        cartViewModel = new ViewModelProvider(this,
                new CartWithInventoryViewModelFactory(CartRetrofitClient.getCartService(), InventoryRetrofitClient.getInventoryService()))
                .get(CartViewModel.class);

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
        // Inflate layout for dialog
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_checkout, null);

        LinearLayout productsContainer = dialogView.findViewById(R.id.dialogProductsContainer);

        // Lấy itemWarehouseIdsMap từ adapter
        Map<Long, String> itemWarehouseIdsMap = adapter.getItemWarehouseIdsMap();

        // Add each selected product to the dialog
        for (Item item : selectedItems) {
            View productView = inflater.inflate(R.layout.cart_item, null);

            ImageView productImage = productView.findViewById(R.id.cartProductImage);
            TextView productName = productView.findViewById(R.id.cartProductName);
            TextView productPrice = productView.findViewById(R.id.cartProductPrice);
            TextView productQuantity = productView.findViewById(R.id.cartProductQuantity);
            TextView productWarehouseIds = productView.findViewById(R.id.cartProductWarehouseIds);
            Button increaseQuantityButton = productView.findViewById(R.id.cartIncreaseQuantityButton);
            Button reduceQuantityButton = productView.findViewById(R.id.cartReduceQuantityButton);
            CheckBox productCheckbox = productView.findViewById(R.id.cartProductCheckbox);

            // Hide the checkbox, buttons, and EditText in the dialog
            productCheckbox.setVisibility(View.GONE);
            increaseQuantityButton.setVisibility(View.GONE);
            reduceQuantityButton.setVisibility(View.GONE);
            productQuantity.setFocusable(false); // Prevent editing in dialog
            productQuantity.setClickable(false); // Prevent clicking on EditText

            // Set product details
            productName.setText(item.getName());
            productPrice.setText("Price: $" + item.getPrice().toString());
            productQuantity.setText("Quantity: " + item.getQuantity());
            productWarehouseIds.setText("Warehouse IDs: " + (itemWarehouseIdsMap.get(item.getProductId()) != null ? itemWarehouseIdsMap.get(item.getProductId()) : "N/A"));

            // Load product image
            if (item.getPrimaryImageUrl() != null && !item.getPrimaryImageUrl().isEmpty()) {
                String fullImageUrl = "http://10.0.2.2:6001" + item.getPrimaryImageUrl();
                Picasso.get().load(fullImageUrl).into(productImage);
            } else {
                String defaultImageUrl = "https://via.placeholder.com/150";
                Picasso.get().load(defaultImageUrl).into(productImage);
            }

            // Add productView to the container
            productsContainer.addView(productView);
        }

        // Create and show dialog
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Checkout", (dialog, which) -> {
                    // Handle checkout action
                    Toast.makeText(CartActivity.this, "Proceeding to checkout...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
