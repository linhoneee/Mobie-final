package com.example.brandtests.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.brandtests.R;
import com.example.brandtests.adapter.CheckoutAdapter;
import com.example.brandtests.model.Item;
import com.example.brandtests.viewmodel.CheckoutViewModel;
import java.util.Arrays;
import java.util.List;

public class CheckoutPage extends AppCompatActivity {
    private CheckoutViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        viewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);

        TextView warehouseIdTextView = findViewById(R.id.selectedWarehouseId);
        ListView selectedItemsListView = findViewById(R.id.selectedItemsListView);
        TextView distanceTextView = findViewById(R.id.distanceTextView);
        TextView tvReceiverName = findViewById(R.id.tvReceiverName);
        TextView tvFullAddress = findViewById(R.id.tvFullAddress);
        TextView tvWarehouseName = findViewById(R.id.tvWarehouseName);
        TextView warehouseAddress = findViewById(R.id.warehouseAddress);
        TextView route = findViewById(R.id.route);
        TextView totalTextView = findViewById(R.id.totalTextView);  // Thêm dòng này

        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        Long userId = sharedPreferences.getLong("UserID", -1);
        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String selectedWarehouseId = getIntent().getStringExtra("selectedWarehouseId");
        List<Long> warehouseIds = Arrays.asList(Long.parseLong(selectedWarehouseId));
        viewModel.calculateDistance(userId, warehouseIds);

        viewModel.getDistanceRecord().observe(this, distanceRecord -> {
            if (distanceRecord != null) {
                tvReceiverName.setText("Receiver Name: " + distanceRecord.getReceiverName());
                String fullAddress = distanceRecord.getProvinceCity() + ", " + distanceRecord.getDistrict() + ", " +
                        distanceRecord.getWard() + ", " + distanceRecord.getStreet();
                tvFullAddress.setText("Address: " + fullAddress);
                tvWarehouseName.setText("Warehouse Name: " + distanceRecord.getWarehouseName());
                distanceTextView.setText("Distance: " + distanceRecord.getDistance() + " km");
                String warehouseFullAddress = distanceRecord.getWarehouseProvinceCity() + ", " + distanceRecord.getWarehouseDistrict() + ", " +
                        distanceRecord.getWarehouseWard() ;
                warehouseAddress.setText("Address: " + warehouseFullAddress);
                route.setText("route: " + distanceRecord.getRoute());
            } else {
                distanceTextView.setText("Failed to fetch distance data");
            }
        });

        List<Item> selectedGroupItems = getIntent().getParcelableArrayListExtra("selectedGroupItems");
        CheckoutAdapter adapter = new CheckoutAdapter(this, selectedGroupItems);
        selectedItemsListView.setAdapter(adapter);
        warehouseIdTextView.setText("Warehouse ID: " + selectedWarehouseId);
        totalTextView.setText("Tổng tiền: $" + String.format("%.2f", adapter.getTotalPrice()));  // Thêm dòng này
    }
}
