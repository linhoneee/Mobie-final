package com.example.brandtests.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.brandtests.R;
import com.example.brandtests.model.Item;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.List;

public class CheckoutAdapter extends ArrayAdapter<Item> {
    private BigDecimal totalPrice = BigDecimal.ZERO;  // Sử dụng BigDecimal để đảm bảo tính chính xác
    private double totalWeight = 0.0;  // Biến để lưu trữ tổng khối lượng

    public CheckoutAdapter(@NonNull Context context, @NonNull List<Item> items) {
        super(context, 0, items);
        calculateTotalPriceAndWeight(items);  // Tính tổng giá và khối lượng
    }

    private void calculateTotalPriceAndWeight(List<Item> items) {
        totalPrice = BigDecimal.ZERO;
        totalWeight = 0.0;
        for (Item item : items) {
            totalPrice = totalPrice.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            totalWeight += item.getWeight() * item.getQuantity();  // Cộng thêm trọng lượng của từng mục
        }
    }

    public double getTotalPrice() {
        return totalPrice.doubleValue();  // Trả về giá trị double từ BigDecimal
    }

    public double getTotalWeight() {
        return totalWeight;  // Trả về tổng khối lượng
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.checkout_item, parent, false);
        }

        Item item = getItem(position);

        if (item != null) {
            TextView productName = convertView.findViewById(R.id.checkoutProductName);
            TextView productPrice = convertView.findViewById(R.id.checkoutProductPrice);
            TextView productQuantity = convertView.findViewById(R.id.checkoutProductQuantity);
            ImageView productImage = convertView.findViewById(R.id.checkoutProductImage);

            productName.setText(item.getName());
            productPrice.setText("Giá: $" + String.format("%.2f", item.getPrice()));
            productQuantity.setText("Số lượng: " + item.getQuantity());

            if (item.getPrimaryImageUrl() != null && !item.getPrimaryImageUrl().isEmpty()) {
                String fullImageUrl = "http://10.0.2.2:6001" + item.getPrimaryImageUrl();
                Picasso.get().load(fullImageUrl).into(productImage);
            } else {
                String defaultImageUrl = "https://via.placeholder.com/150";
                Picasso.get().load(defaultImageUrl).into(productImage);
            }
        }

        return convertView;
    }
}
