package com.example.brandtests.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.example.brandtests.Login.LoginActivity;
import com.example.brandtests.R;
import com.example.brandtests.adapter.BannerAdapter;
import com.example.brandtests.adapter.ProductAdapter;
import com.example.brandtests.model.Item;
import com.example.brandtests.model.ProductDTOuser;
import com.example.brandtests.service.CartRetrofitClient;
import com.example.brandtests.service.InventoryRetrofitClient;
import com.example.brandtests.service.RetrofitClient;
import com.example.brandtests.viewmodel.CartViewModel;
import com.example.brandtests.viewmodel.CartViewModelFactory;
import com.example.brandtests.viewmodel.ProductViewModel;
import com.example.brandtests.viewmodel.ProductViewModelFactory;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private ProductViewModel productViewModel;
    private CartViewModel cartViewModel;
    private SharedPreferences sharedPreferences;
    private Long userId;

    private Button loginButton, logoutButton;
    private ImageButton cartButton, chatButton , profileButon;
    private ViewPager2 bannerViewPager;
    private Handler bannerHandler;
    private Runnable bannerRunnable;
    private LinearLayout headerButtonsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong("UserID", -1);

        loginButton = findViewById(R.id.bbuttonLogin);
        logoutButton = findViewById(R.id.bbuttonLogout);
        cartButton = findViewById(R.id.bbuttonCart);
        chatButton = findViewById(R.id.bbuttonChat);
        bannerViewPager = findViewById(R.id.bannerViewPager);
        profileButon = findViewById(R.id.bbuttonProfile);
        headerButtonsContainer = findViewById(R.id.headerButtonsContainer); // Nhóm button

        setupBanner();

        ListView listView = findViewById(R.id.listViewProducts);
        checkIfLoggedIn();

        productViewModel = new ViewModelProvider(this, new ProductViewModelFactory(RetrofitClient.getProductService()))
                .get(ProductViewModel.class);

        cartViewModel = new ViewModelProvider(
                this,
                new CartViewModelFactory(CartRetrofitClient.getCartService(), InventoryRetrofitClient.getInventoryService())
        ).get(CartViewModel.class);

        cartViewModel.fetchInventories();

        productViewModel.getProducts().observe(this, products -> {
            if (products != null) {
                cartViewModel.getInventories().observe(this, inventories -> {
                    if (inventories != null) {
                        ProductAdapter adapter = new ProductAdapter(this, products, userId, inventories, productDTOuser -> {
                            if (userId == -1) {
                                Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ hàng.", Toast.LENGTH_SHORT).show();
                            } else if (productDTOuser != null && productDTOuser.getProduct() != null) {
                                Item item = new Item(
                                        productDTOuser.getProduct().getId(),
                                        productDTOuser.getProduct().getProductName(),
                                        BigDecimal.valueOf(productDTOuser.getProduct().getPrice()),
                                        1,
                                        productDTOuser.getProduct().getWeight().intValue(),
                                        productDTOuser.getPrimaryImage() != null ? productDTOuser.getPrimaryImage().getUrl() : ""
                                );
                                cartViewModel.addItemToCart(userId, item);
                            }
                        });

                        listView.setAdapter(adapter);
                    }
                });
            }
        });

        cartViewModel.getAddItemResult().observe(this, resultMessage -> {
            Toast.makeText(ProductActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
        });

        loginButton.setOnClickListener(v -> startActivity(new Intent(ProductActivity.this, LoginActivity.class)));
        logoutButton.setOnClickListener(v -> logoutUser());

        cartButton.setOnClickListener(v -> {
            if (userId != -1) {
                Intent intent = new Intent(ProductActivity.this, CartActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            } else {
                Toast.makeText(ProductActivity.this, "Người dùng chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            }
        });

        chatButton.setOnClickListener(v -> {
            if (userId != -1) {
                Intent intent = new Intent(ProductActivity.this, ChatActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            } else {
                Toast.makeText(ProductActivity.this, "Người dùng chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            }
        });

        profileButon.setOnClickListener(v -> {
            if (userId != -1) {
                Intent intent = new Intent(ProductActivity.this, ProfileActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            } else {
                Toast.makeText(ProductActivity.this, "Người dùng chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBanner() {
        List<Integer> bannerImages = Arrays.asList(R.drawable.q15, R.drawable.q15, R.drawable.q16);
        BannerAdapter bannerAdapter = new BannerAdapter(bannerImages);
        bannerViewPager.setAdapter(bannerAdapter);

        bannerHandler = new Handler(Looper.getMainLooper());
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                int nextItem = (bannerViewPager.getCurrentItem() + 1) % bannerImages.size();
                bannerViewPager.setCurrentItem(nextItem, true);
                bannerHandler.postDelayed(this, 4000);
            }
        };
        bannerHandler.postDelayed(bannerRunnable, 4000);

        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                bannerHandler.removeCallbacks(bannerRunnable);
                bannerHandler.postDelayed(bannerRunnable, 4000);
            }
        });
    }

    private void checkIfLoggedIn() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        loginButton.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
        logoutButton.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        headerButtonsContainer.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
    }

    private void logoutUser() {
        sharedPreferences.edit().clear().apply();
        Toast.makeText(ProductActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        checkIfLoggedIn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bannerHandler.removeCallbacks(bannerRunnable);
    }
}
