package com.example.brandtests.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.brandtests.MainActivity;
import com.example.brandtests.R;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private EditText emailEditText, passwordEditText;
    private Button loginButton, logoutButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        logoutButton = findViewById(R.id.buttonLogout);

        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);

        // Kiểm tra nếu đã đăng nhập
        checkIfLoggedIn();

        // Khởi tạo UserService từ RetrofitClient
        UserService userService = RetrofitClient.getUserService();

        // Sử dụng UserViewModelFactory để khởi tạo UserViewModel
        UserViewModelFactory factory = new UserViewModelFactory(userService);
        userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);

        userViewModel.getLoginResponse().observe(this, new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> response) {
                if (response != null && response.containsKey("token")) {
                    // Lưu thông tin đăng nhập khi đăng nhập thành công
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", emailEditText.getText().toString());
                    editor.putString("token", response.get("token"));
                    editor.putBoolean("isLoggedIn", true);

                    // Lưu UserID nếu có trong phản hồi từ server
                    if (response.containsKey("UserID")) {
                        editor.putLong("UserID", Long.parseLong(response.get("UserID")));
                        Log.d("LoginActivity", "UserID saved: " + response.get("UserID"));
                    } else {
                        Log.d("LoginActivity", "UserID not found in response");
                    }

                    editor.apply();

                    // Log dữ liệu phản hồi từ BE
                    Log.d("LoginActivity", "Đăng nhập thành công: " + response.toString());

                    // Hiển thị thông báo đăng nhập thành công
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                    // Quay lại MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Kết thúc LoginActivity để quay lại MainActivity

                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    User loginUser = new User(); // Tạo đối tượng User
                    loginUser.setEmail(email);
                    loginUser.setPassword(password);
                    userViewModel.loginUser(loginUser); // Truyền đối tượng User vào đây
                } else {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void checkIfLoggedIn() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // Đã đăng nhập, ẩn nút đăng nhập và hiển thị nút đăng xuất
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);

            // Log thông tin đã lưu trong SharedPreferences
            String savedEmail = sharedPreferences.getString("email", "No email found");
            String savedToken = sharedPreferences.getString("token", "No token found");
            Long savedUserId = sharedPreferences.getLong("UserID", -1L);
            Log.d("LoginActivity", "Thông tin trong SharedPreferences - Email: " + savedEmail + ", Token: " + savedToken + ", UserID: " + savedUserId);

        } else {
            // Chưa đăng nhập, hiển thị nút đăng nhập và ẩn nút đăng xuất
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }
    }

    private void logoutUser() {
        // Xóa thông tin đăng nhập
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(LoginActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

        // Hiển thị lại nút đăng nhập và ẩn nút đăng xuất
        loginButton.setVisibility(View.VISIBLE);
        logoutButton.setVisibility(View.GONE);
    }
}
