package com.example.brandtests.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.brandtests.R;
import com.example.brandtests.adapter.ProductAdapter;
import com.example.brandtests.model.Item;
import com.example.brandtests.model.ProductDTOuser;
import com.example.brandtests.service.CartRetrofitClient;
import com.example.brandtests.service.RetrofitClient;
import com.example.brandtests.viewmodel.CartViewModel;
import com.example.brandtests.viewmodel.CartViewModelFactory;
import com.example.brandtests.viewmodel.ProductViewModel;
import com.example.brandtests.viewmodel.ProductViewModelFactory;

import java.math.BigDecimal;

public class ProductActivity extends AppCompatActivity {

    private ProductViewModel productViewModel;
    private CartViewModel cartViewModel;
    private SharedPreferences sharedPreferences;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong("UserID", -1);

        ListView listView = findViewById(R.id.listViewProducts);

        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        productViewModel = new ViewModelProvider(this, new ProductViewModelFactory(RetrofitClient.getProductService()))
                .get(ProductViewModel.class);

        cartViewModel = new ViewModelProvider(this, new CartViewModelFactory(CartRetrofitClient.getCartService()))
                .get(CartViewModel.class);

        productViewModel.getProducts().observe(this, products -> {
            if (products != null) {
                ProductAdapter adapter = new ProductAdapter(this, products, userId, productDTOuser -> {
                    if (productDTOuser != null && productDTOuser.getProduct() != null) {
                        Item item = new Item(
                                productDTOuser.getProduct().getId(),
                                productDTOuser.getProduct().getProductName(),
                                BigDecimal.valueOf(productDTOuser.getProduct().getPrice()),
                                1, // Số lượng mặc định là 1
                                productDTOuser.getProduct().getWeight().intValue(),
                                productDTOuser.getPrimaryImage() != null ? productDTOuser.getPrimaryImage().getUrl() : ""
                        );
                        cartViewModel.addItemToCart(userId, item);
                    }
                });
                listView.setAdapter(adapter);
            }
        });

        // Lắng nghe kết quả thêm vào giỏ hàng và hiển thị thông báo cho người dùng
        cartViewModel.getAddItemResult().observe(this, resultMessage -> {
            Toast.makeText(ProductActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
        });
    }
}
