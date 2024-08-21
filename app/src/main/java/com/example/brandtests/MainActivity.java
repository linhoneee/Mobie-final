package com.example.brandtests;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.brandtests.Login.LoginActivity;
import com.example.brandtests.view.BrandActivity;
import com.example.brandtests.view.CartActivity;
import com.example.brandtests.view.ChatActivity; // Import ChatActivity
import com.example.brandtests.view.ProductActivity;
import com.example.brandtests.viewmodel.CartViewModel;
import com.example.brandtests.viewmodel.CartViewModelFactory;
import com.example.brandtests.service.CartRetrofitClient;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Button loginButton, logoutButton, brandButton, productButton, cartButton, chatButton;
    private CartViewModel cartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các Button
        loginButton = findViewById(R.id.buttonLogin);
        logoutButton = findViewById(R.id.buttonLogout);
        brandButton = findViewById(R.id.buttonBrand);
        productButton = findViewById(R.id.buttonProducts);
        cartButton = findViewById(R.id.buttonCart);
        chatButton = findViewById(R.id.buttonChat); // Nút Chat

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);

        // Khởi tạo CartViewModel
        cartViewModel = new ViewModelProvider(this, new CartViewModelFactory(CartRetrofitClient.getCartService()))
                .get(CartViewModel.class);

        // Kiểm tra nếu người dùng đã đăng nhập
        checkIfLoggedIn();

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> logoutUser());

        brandButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BrandActivity.class);
            startActivity(intent);
        });

        productButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductActivity.class);
            startActivity(intent);
        });

        cartButton.setOnClickListener(v -> {
            Long userId = getUserIdFromPreferences();
            if (userId != null && userId != -1) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "User not logged in!", Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện click vào nút Chat
        chatButton.setOnClickListener(v -> {
            Long userId = getUserIdFromPreferences();
            if (userId != null && userId != -1) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("userId", userId); // Truyền userId vào ChatActivity
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "User not logged in!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Long getUserIdFromPreferences() {
        return sharedPreferences.getLong("UserID", -1); // Đảm bảo rằng bạn đang lưu trữ UserID với khóa "UserID"
    }

    private void checkIfLoggedIn() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            cartButton.setVisibility(View.VISIBLE);
            chatButton.setVisibility(View.VISIBLE); // Hiển thị nút Chat nếu đã đăng nhập
        } else {
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            cartButton.setVisibility(View.GONE);
            chatButton.setVisibility(View.GONE); // Ẩn nút Chat nếu chưa đăng nhập
        }
        brandButton.setVisibility(View.VISIBLE);
        productButton.setVisibility(View.VISIBLE);
    }

    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        checkIfLoggedIn();
    }
}
