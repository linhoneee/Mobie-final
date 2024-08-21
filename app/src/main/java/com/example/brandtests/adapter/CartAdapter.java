package com.example.brandtests.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.brandtests.R;
import com.example.brandtests.model.Inventory;
import com.example.brandtests.model.Item;
import com.example.brandtests.viewmodel.CartViewModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class CartAdapter extends ArrayAdapter<Item> {

    private CartViewModel cartViewModel;
    private Long userId;
    private Map<Long, String> itemWarehouseIdsMap;

    public CartAdapter(@NonNull Context context, @NonNull List<Item> items, Long userId, List<Inventory> inventories) {
        super(context, 0, items);
        this.userId = userId;
        cartViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(CartViewModel.class);

        // Xử lý các inventories và tạo Map chứa warehouseIds cho mỗi sản phẩm
        itemWarehouseIdsMap = inventories.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getProductId,
                        Collectors.mapping(
                                inventory -> inventory.getWarehouseId().toString(),
                                Collectors.joining(",")
                        )
                ));

        // Lắng nghe kết quả thêm vào giỏ hàng và hiển thị thông báo
        cartViewModel.getAddItemResult().observe((LifecycleOwner) context, result -> {
            if (result != null && !result.isEmpty()) {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cart_item, parent, false);
        }

        Item item = getItem(position);
        String warehouseIds = itemWarehouseIdsMap.get(item.getProductId());

        if (item != null) {
            TextView productName = convertView.findViewById(R.id.cartProductName);
            TextView productPrice = convertView.findViewById(R.id.cartProductPrice);
            EditText productQuantity = convertView.findViewById(R.id.cartProductQuantity);
            TextView productWarehouseIds = convertView.findViewById(R.id.cartProductWarehouseIds);
            Button increaseQuantityButton = convertView.findViewById(R.id.cartIncreaseQuantityButton);
            Button reduceQuantityButton = convertView.findViewById(R.id.cartReduceQuantityButton);

            productName.setText(item.getName());
            productPrice.setText("Price: $" + item.getPrice().toString());
            productQuantity.setText(String.valueOf(item.getQuantity()));
            productWarehouseIds.setText("Warehouse IDs: " + (warehouseIds != null ? warehouseIds : "N/A"));

            // Đổi màu nếu hết hàng
            if (warehouseIds == null || warehouseIds.isEmpty()) {
                convertView.setBackgroundColor(Color.RED);
                increaseQuantityButton.setEnabled(false);
                reduceQuantityButton.setEnabled(false);

            } else {
                convertView.setBackgroundColor(Color.WHITE);
                increaseQuantityButton.setEnabled(true);
                reduceQuantityButton.setEnabled(true);

            }

            // Lưu lại số lượng ban đầu
            final int[] originalQuantity = {item.getQuantity()};

            // Sử dụng TextWatcher để theo dõi thay đổi số lượng
            productQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Chỉ xử lý khi EditText này mất tiêu điểm
                }
            });

            // Sự kiện khi EditText mất tiêu điểm
            productQuantity.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String input = productQuantity.getText().toString();
                    if (!input.isEmpty()) {
                        int newQuantity = Integer.parseInt(input);
                        int difference = newQuantity - originalQuantity[0];

                        if (difference > 0) {
                            // Số lượng tăng, gọi addItemToCart với số lượng chênh lệch
                            Item newItem = new Item(
                                    item.getProductId(),
                                    item.getName(),
                                    item.getPrice(),
                                    difference, // Số lượng thêm vào là sự khác biệt
                                    item.getWeight(),
                                    item.getPrimaryImageUrl()
                            );
                            cartViewModel.addItemToCart(userId, newItem);
                        } else if (difference < 0) {
                            // Số lượng giảm, gọi removeItemFromCart với số lượng chênh lệch
                            Item newItem = new Item(
                                    item.getProductId(),
                                    item.getName(),
                                    item.getPrice(),
                                    Math.abs(difference), // Số lượng xóa là giá trị tuyệt đối của sự khác biệt
                                    item.getWeight(),
                                    item.getPrimaryImageUrl()
                            );
                            cartViewModel.removeItemFromCart(userId, newItem);
                        }

                        // Cập nhật số lượng ban đầu
                        originalQuantity[0] = newQuantity;
                    }
                }
            });

            // Sự kiện click vào nút "+" để tăng số lượng của sản phẩm
            increaseQuantityButton.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() + 1;
                productQuantity.setText(String.valueOf(newQuantity));
                productQuantity.clearFocus();  // Để thực hiện tính toán ngay khi người dùng nhấn vào nút "+"
            });

            // Sự kiện click vào nút "-"
            reduceQuantityButton.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() - 1;
                if (newQuantity > 0) {
                    productQuantity.setText(String.valueOf(newQuantity));
                    productQuantity.clearFocus();  // Để thực hiện tính toán ngay khi người dùng nhấn vào nút "-"
                }
            });
        }

        return convertView;
    }
}
