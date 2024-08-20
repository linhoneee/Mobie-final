package com.example.brandtests.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.brandtests.R;
import com.example.brandtests.model.Item;
import com.example.brandtests.viewmodel.CartViewModel;

import java.math.BigDecimal;
import java.util.List;

public class CartAdapter extends ArrayAdapter<Item> {

    private CartViewModel cartViewModel;
    private Long userId;

    public CartAdapter(@NonNull Context context, @NonNull List<Item> items, Long userId) {
        super(context, 0, items);
        this.userId = userId;
        cartViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(CartViewModel.class);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cart_item, parent, false);
        }

        Item item = getItem(position);

        if (item != null) {
            TextView productName = convertView.findViewById(R.id.cartProductName);
            TextView productPrice = convertView.findViewById(R.id.cartProductPrice);
            TextView productQuantity = convertView.findViewById(R.id.cartProductQuantity);
            Button increaseQuantityButton = convertView.findViewById(R.id.cartIncreaseQuantityButton);

            productName.setText(item.getName());
            productPrice.setText("Price: $" + item.getPrice().toString());
            productQuantity.setText("Quantity: " + item.getQuantity());

            // Sự kiện click vào nút "+" để tăng số lượng của sản phẩm
            increaseQuantityButton.setOnClickListener(v -> {
                // Tạo một bản sao của sản phẩm hiện tại với quantity = 1
                Item newItem = new Item(
                        item.getProductId(),
                        item.getName(),
                        item.getPrice(),
                        1, // Số lượng luôn là 1 khi nhấn nút "+"
                        item.getWeight(),
                        item.getPrimaryImageUrl()
                );
                cartViewModel.addItemToCart(userId, newItem);

                // Sau khi thêm vào giỏ hàng thành công, cập nhật quantity trong UI
                item.setQuantity(item.getQuantity() + 1); // Tăng số lượng hiện tại thêm 1
                notifyDataSetChanged(); // Cập nhật lại giao diện
            });
        }

        return convertView;
    }
}
