package com.example.brandtests.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.brandtests.R;
import com.example.brandtests.model.Item;
import com.example.brandtests.model.ProductDTOuser;
import com.example.brandtests.service.CartRetrofitClient;
import com.example.brandtests.viewmodel.CartViewModel;
import com.example.brandtests.viewmodel.CartViewModelFactory;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.List;
public class ProductAdapter extends ArrayAdapter<ProductDTOuser> {

    private static final String TAG = "ProductAdapter";
    private CartViewModel cartViewModel;
    private Long userId;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onAddToCartClick(ProductDTOuser productDTOuser);
    }

    public ProductAdapter(@NonNull Context context, @NonNull List<ProductDTOuser> products, Long userId, OnProductClickListener listener) {
        super(context, 0, products);
        Log.d(TAG, "ProductAdapter: Adapter initialized with " + products.size() + " products");
        this.userId = userId;
        this.listener = listener;

        cartViewModel = new ViewModelProvider((ViewModelStoreOwner) context, new CartViewModelFactory(CartRetrofitClient.getCartService()))
                .get(CartViewModel.class);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            Log.d(TAG, "getView: Inflating new view for position " + position);
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_item, parent, false);
        }

        ProductDTOuser productDTOuser = getItem(position); // Lấy đối tượng ProductDTOuser tại vị trí position

        if (productDTOuser != null && productDTOuser.getProduct() != null) {
            Log.d(TAG, "getView: Populating view for product " + productDTOuser.getProduct().getProductName());

            TextView productName = convertView.findViewById(R.id.productName);
            TextView productDescription = convertView.findViewById(R.id.productDescription);
            TextView productPrice = convertView.findViewById(R.id.productPrice);
            ImageView productImage = convertView.findViewById(R.id.productImage);
            Button addToCartButton = convertView.findViewById(R.id.addToCartButton);

            productName.setText(productDTOuser.getProduct().getProductName());
            productDescription.setText(productDTOuser.getProduct().getDescriptionDetails());
            productPrice.setText(String.format("$%.2f", productDTOuser.getProduct().getPrice()));

            // Sử dụng Picasso để tải ảnh từ URL
            if (productDTOuser.getPrimaryImage() != null && productDTOuser.getPrimaryImage().getUrl() != null) {
                String fullImageUrl = "http://10.0.2.2:6001" + productDTOuser.getPrimaryImage().getUrl();
                Picasso.get().load(fullImageUrl).into(productImage);
            } else {
                String defaultImageUrl = "https://via.placeholder.com/150";
                Picasso.get().load(defaultImageUrl).into(productImage);
            }

            addToCartButton.setOnClickListener(v -> {
                // Tạo đối tượng Item từ dữ liệu của ProductDTOuser
                Item item = new Item(
                        productDTOuser.getProduct().getId(),
                        productDTOuser.getProduct().getProductName(),
                        BigDecimal.valueOf(productDTOuser.getProduct().getPrice()),
                        1, // Số lượng mặc định là 1
                        productDTOuser.getProduct().getWeight().intValue(),
                        productDTOuser.getPrimaryImage() != null ? productDTOuser.getPrimaryImage().getUrl() : ""
                );
                // Gọi phương thức addItemToCart của CartViewModel để thêm sản phẩm vào giỏ hàng
                cartViewModel.addItemToCart(userId, item);

                // Thực hiện hành động sau khi thêm vào giỏ hàng nếu có listener
                if (listener != null) {
                    listener.onAddToCartClick(productDTOuser);
                }
            });
        } else {
            Log.e(TAG, "getView: ProductDTOuser is null at position " + position);
        }

        return convertView;
    }
}
