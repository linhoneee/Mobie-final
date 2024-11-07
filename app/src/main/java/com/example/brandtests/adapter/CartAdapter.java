package com.example.brandtests.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.brandtests.R;
import com.example.brandtests.model.Inventory;
import com.example.brandtests.model.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CartAdapter extends ArrayAdapter<Item> {

    private Long userId;
    private List<Item> selectedItems = new ArrayList<>();
    private Map<Long, String> itemWarehouseIdsMap;

    // Định nghĩa các callback
    public interface OnQuantityChangeListener {
        void onQuantityChanged(Item item, int newQuantity);
    }

    public interface OnItemCheckListener {
        void onItemChecked(Item item, boolean isChecked);
    }

    private OnQuantityChangeListener quantityChangeListener;
    private OnItemCheckListener itemCheckListener;

    // Constructor
    public CartAdapter(@NonNull Context context, @NonNull List<Item> items, Long userId,
                       List<Inventory> inventories, OnQuantityChangeListener quantityChangeListener,
                       OnItemCheckListener itemCheckListener) {
        super(context, 0, items);
        this.userId = userId;
        this.quantityChangeListener = quantityChangeListener;
        this.itemCheckListener = itemCheckListener;

        itemWarehouseIdsMap = inventories.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getProductId,
                        Collectors.mapping(
                                inventory -> inventory.getWarehouseId().toString(),
                                Collectors.joining(",")
                        )
                ));
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cart_item, parent, false);
        }

        Item item = getItem(position);
        String warehouseIds = itemWarehouseIdsMap != null ? itemWarehouseIdsMap.get(item.getProductId()) : null;

        if (item != null) {
            TextView productName = convertView.findViewById(R.id.cartProductName);
            TextView productPrice = convertView.findViewById(R.id.cartProductPrice);
            EditText productQuantity = convertView.findViewById(R.id.cartProductQuantity);
            TextView productWarehouseIds = convertView.findViewById(R.id.cartProductWarehouseIds);
            Button increaseQuantityButton = convertView.findViewById(R.id.cartIncreaseQuantityButton);
            Button reduceQuantityButton = convertView.findViewById(R.id.cartReduceQuantityButton);
            ImageView productImage = convertView.findViewById(R.id.cartProductImage);
            CheckBox productCheckbox = convertView.findViewById(R.id.cartProductCheckbox);

            productName.setText(item.getName());
            productPrice.setText("Price: $" + item.getPrice().toString());
            productQuantity.setText(String.valueOf(item.getQuantity()));
            productWarehouseIds.setText("Warehouse IDs: " + (warehouseIds != null ? warehouseIds : "N/A"));

            // Sử dụng Picasso để tải ảnh từ URL
            if (item.getPrimaryImageUrl() != null && !item.getPrimaryImageUrl().isEmpty()) {
                String fullImageUrl = "http://10.0.2.2:6001" + item.getPrimaryImageUrl();
                Picasso.get().load(fullImageUrl).into(productImage);
            } else {
                String defaultImageUrl = "https://via.placeholder.com/150";
                Picasso.get().load(defaultImageUrl).into(productImage);
            }

            // Kiểm tra có hàng hay không để hiển thị màu nền
            if (warehouseIds == null || warehouseIds.isEmpty()) {
                convertView.setBackgroundColor(Color.RED);
                increaseQuantityButton.setEnabled(false);
                reduceQuantityButton.setEnabled(false);
                productCheckbox.setEnabled(false);
                productQuantity.setEnabled(false);
            } else {
                convertView.setBackgroundColor(Color.WHITE);
                increaseQuantityButton.setEnabled(true);
                reduceQuantityButton.setEnabled(true);
                productCheckbox.setEnabled(true);
                productQuantity.setEnabled(true);
            }

            // Sự kiện khi tăng số lượng
            increaseQuantityButton.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() + 1;
                productQuantity.setText(String.valueOf(newQuantity)); // Cập nhật số lượng hiển thị
                if (quantityChangeListener != null) {
                    quantityChangeListener.onQuantityChanged(item, newQuantity);
                }
            });

            // Sự kiện khi giảm số lượng
            reduceQuantityButton.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() - 1;
                if (newQuantity > 0) {
                    productQuantity.setText(String.valueOf(newQuantity)); // Cập nhật số lượng hiển thị
                    if (quantityChangeListener != null) {
                        quantityChangeListener.onQuantityChanged(item, newQuantity);
                    }
                }
            });

            // Sự kiện khi chọn sản phẩm
            productCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (itemCheckListener != null) {
                    itemCheckListener.onItemChecked(item, isChecked);
                }
            });

            // Sự kiện khi EditText mất tiêu điểm
            productQuantity.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String input = productQuantity.getText().toString();
                    if (!input.isEmpty()) {
                        int newQuantity = Integer.parseInt(input);
                        if (quantityChangeListener != null) {
                            quantityChangeListener.onQuantityChanged(item, newQuantity);
                        }
                    }
                }
            });

            // TextWatcher để theo dõi thay đổi trong EditText
            productQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Logic xử lý khi văn bản thay đổi nếu cần thiết
                }
            });
        }
        return convertView;
    }

    public List<Item> getSelectedItems() {
        return selectedItems;
    }

    public Map<Long, String> getItemWarehouseIdsMap() {
        return itemWarehouseIdsMap;
    }
}
