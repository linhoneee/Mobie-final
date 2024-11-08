package com.example.brandtests.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.brandtests.R;
import com.example.brandtests.model.Inventory;
import com.example.brandtests.model.Item;
import com.example.brandtests.model.ProductDTOuser;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends ArrayAdapter<ProductDTOuser> {

    private Long userId;
    private OnProductClickListener listener;
    private Map<Long, Integer> inventoryMap = new HashMap<>();

    public interface OnProductClickListener {
        void onAddToCartClick(ProductDTOuser productDTOuser);
    }

    public ProductAdapter(@NonNull Context context, @NonNull List<ProductDTOuser> products, Long userId,
                          List<Inventory> inventories, OnProductClickListener listener) {
        super(context, 0, products);
        this.userId = userId;
        this.listener = listener;

        // Tạo bản đồ inventoryMap để lưu thông tin tồn kho cho mỗi sản phẩm
        for (Inventory inventory : inventories) {
            inventoryMap.put(inventory.getProductId(), inventory.getQuantity());
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_item, parent, false);
        }

        ProductDTOuser productDTOuser = getItem(position);

        if (productDTOuser != null && productDTOuser.getProduct() != null) {
            TextView productName = convertView.findViewById(R.id.productName);
            TextView productDescription = convertView.findViewById(R.id.productDescription);
            TextView productPrice = convertView.findViewById(R.id.productPrice);
            ImageView productImage = convertView.findViewById(R.id.productImage);
            Button addToCartButton = convertView.findViewById(R.id.addToCartButton);

            productName.setText(productDTOuser.getProduct().getProductName());
            productDescription.setText(productDTOuser.getProduct().getDescriptionDetails());
            productPrice.setText(String.format("$%.2f", productDTOuser.getProduct().getPrice()));

            if (productDTOuser.getPrimaryImage() != null && productDTOuser.getPrimaryImage().getUrl() != null) {
                String fullImageUrl = "http://10.0.2.2:6001" + productDTOuser.getPrimaryImage().getUrl();
                Picasso.get().load(fullImageUrl).into(productImage);
            } else {
                String defaultImageUrl = "https://via.placeholder.com/150";
                Picasso.get().load(defaultImageUrl).into(productImage);
            }

            Integer stock = inventoryMap.get(productDTOuser.getProduct().getId());
            if (stock == null || stock <= 0) {
                convertView.setBackgroundColor(Color.GRAY);
                addToCartButton.setEnabled(false);
                addToCartButton.setText("Hết hàng");
            } else {
                convertView.setBackgroundColor(Color.WHITE);
                addToCartButton.setEnabled(true);
                addToCartButton.setText("Thêm vào giỏ hàng");
            }

            addToCartButton.setOnClickListener(v -> {
                if (stock != null && stock > 0) {
                    Item item = new Item(
                            productDTOuser.getProduct().getId(),
                            productDTOuser.getProduct().getProductName(),
                            BigDecimal.valueOf(productDTOuser.getProduct().getPrice()),
                            1,
                            productDTOuser.getProduct().getWeight().intValue(),
                            productDTOuser.getPrimaryImage() != null ? productDTOuser.getPrimaryImage().getUrl() : ""
                    );
                    if (listener != null) {
                        listener.onAddToCartClick(productDTOuser);
                    }
                } else {
                    Toast.makeText(getContext(), "Sản phẩm này đã hết hàng!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return convertView;
    }
}
