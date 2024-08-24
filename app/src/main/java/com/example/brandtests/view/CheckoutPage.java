package com.example.brandtests.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brandtests.R;
import com.example.brandtests.adapter.CheckoutAdapter;
import com.example.brandtests.model.Item;

import java.util.List;

public class CheckoutPage extends AppCompatActivity {

    private static final String TAG = "CheckoutPage";
    private String selectedWarehouseId;
    private List<Item> selectedGroupItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Nhận dữ liệu từ Intent
        selectedWarehouseId = getIntent().getStringExtra("selectedWarehouseId");
        selectedGroupItems = getIntent().getParcelableArrayListExtra("selectedGroupItems");

        // Log dữ liệu để kiểm tra
        Log.d(TAG, "Selected Warehouse ID: " + selectedWarehouseId);
        if (selectedGroupItems != null) {
            Log.d(TAG, "Number of selected items: " + selectedGroupItems.size());
        } else {
            Log.e(TAG, "Selected group items is null");
        }

        // Kiểm tra xem dữ liệu có được nhận chính xác không
        if (selectedWarehouseId == null || selectedGroupItems == null) {
            Toast.makeText(this, "Data not received correctly", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị dữ liệu lên giao diện người dùng
        TextView warehouseIdTextView = findViewById(R.id.selectedWarehouseId);
        TextView checkoutTitle = findViewById(R.id.checkoutTitle);
        TextView itemLabel = findViewById(R.id.itemLabel);
        ListView selectedItemsListView = findViewById(R.id.selectedItemsListView);

        checkoutTitle.setText("Checkout");
        itemLabel.setText("Item");
        warehouseIdTextView.setText("Warehouse ID: " + selectedWarehouseId);

        // Sử dụng CheckoutAdapter để hiển thị danh sách các sản phẩm đã chọn
        CheckoutAdapter adapter = new CheckoutAdapter(this, selectedGroupItems);
        selectedItemsListView.setAdapter(adapter);
    }
}
