package com.example.brandtests.view;

import android.os.Bundle;
import android.widget.ListView;
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

import java.util.List;

public class CartActivity extends AppCompatActivity {

    private CartViewModel cartViewModel;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ListView cartListView = findViewById(R.id.cartListView);
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
                            CartAdapter adapter = new CartAdapter(this, items, userId, inventories);
                            cartListView.setAdapter(adapter);
                        }
                    });
                } else {
                    Toast.makeText(CartActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(CartActivity.this, "Invalid user ID", Toast.LENGTH_SHORT).show();
        }
    }
}
