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

import java.util.List;

public class CheckoutAdapter extends ArrayAdapter<Item> {

    public CheckoutAdapter(@NonNull Context context, @NonNull List<Item> items) {
        super(context, 0, items);
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
            productPrice.setText("Price: $" + item.getPrice().toString());
            productQuantity.setText("Quantity: " + item.getQuantity());

            // Sử dụng Picasso để tải ảnh từ URL
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
