package com.example.brandtests.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CartAdapter extends ArrayAdapter<Item> {

    private static final String TAG = "CartAdapter";
    private CartViewModel cartViewModel;
    private Long userId;
    private Map<Long, String> itemWarehouseIdsMap;
    private List<Item> selectedItems = new ArrayList<>(); // Danh sách sản phẩm đã chọn

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

            // Đổi màu nếu hết hàng
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

            // Sự kiện click vào checkbox
            productCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedItems.add(item);
                } else {
                    selectedItems.remove(item);
                }
            });

            // Sự kiện click vào nút "+" để tăng số lượng của sản phẩm
            increaseQuantityButton.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() + 1;
                item.setQuantity(newQuantity); // Cập nhật số lượng sản phẩm

                // Gọi API để thêm sản phẩm vào giỏ hàng
                Item newItem = new Item(
                        item.getProductId(),
                        item.getName(),
                        item.getPrice(),
                        1, // Thêm một sản phẩm
                        item.getWeight(),
                        item.getPrimaryImageUrl()
                );
                cartViewModel.addItemToCart(userId, newItem);

                // Cập nhật lại giao diện
                productQuantity.setText(String.valueOf(newQuantity));
            });

            // Sự kiện click vào nút "-" để giảm số lượng của sản phẩm
            reduceQuantityButton.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() - 1;
                if (newQuantity > 0) {
                    item.setQuantity(newQuantity); // Cập nhật số lượng sản phẩm

                    // Gọi API để xóa sản phẩm khỏi giỏ hàng
                    Item newItem = new Item(
                            item.getProductId(),
                            item.getName(),
                            item.getPrice(),
                            1, // Giảm một sản phẩm
                            item.getWeight(),
                            item.getPrimaryImageUrl()
                    );
                    cartViewModel.removeItemFromCart(userId, newItem);

                    // Cập nhật lại giao diện
                    productQuantity.setText(String.valueOf(newQuantity));
                } else {
                    Toast.makeText(getContext(), "Số lượng không thể nhỏ hơn 1", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "getView: Item is null at position " + position);
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
