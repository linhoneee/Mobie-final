package com.example.brandtests.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.brandtests.R;
import com.example.brandtests.adapter.CheckoutAdapter;
import com.example.brandtests.model.DistanceData;
import com.example.brandtests.model.DistanceRecord;
import com.example.brandtests.model.Item;
import com.example.brandtests.model.PaymentRequest;
import com.example.brandtests.service.PaymentService;
import com.example.brandtests.service.PaymentRetrofitClient;
import com.example.brandtests.viewmodel.CheckoutViewModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutPage extends AppCompatActivity {
    private CheckoutViewModel viewModel;
    private SharedPreferences sharedPreferences;
    private Long userId;
    private static final String TAG = "CheckoutPage";

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
        TextView totalTextView = findViewById(R.id.totalTextView);
        Button checkoutButton = findViewById(R.id.checkoutButton);

        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong("UserID", -1);
        Log.d(TAG, "Retrieved UserID from SharedPreferences: " + userId);

        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String selectedWarehouseId = getIntent().getStringExtra("selectedWarehouseId");
        List<Item> selectedGroupItems = getIntent().getParcelableArrayListExtra("selectedGroupItems");
        CheckoutAdapter adapter = new CheckoutAdapter(this, selectedGroupItems);
        selectedItemsListView.setAdapter(adapter);
        warehouseIdTextView.setText("Warehouse ID: " + selectedWarehouseId);
        totalTextView.setText("Tổng tiền: $" + String.format("%.2f", adapter.getTotalPrice()));

        viewModel.calculateDistance(userId, Arrays.asList(Long.parseLong(selectedWarehouseId)));

        viewModel.getDistanceRecord().observe(this, distanceRecord -> {
            if (distanceRecord != null) {
                tvReceiverName.setText("Receiver Name: " + distanceRecord.getReceiverName());
                String fullAddress = distanceRecord.getProvinceCity() + ", " + distanceRecord.getDistrict() + ", " +
                        distanceRecord.getWard() + ", " + distanceRecord.getStreet();
                tvFullAddress.setText("Address: " + fullAddress);
                tvWarehouseName.setText("Warehouse Name: " + distanceRecord.getWarehouseName());
                distanceTextView.setText("Distance: " + distanceRecord.getDistance() + " km");
                String warehouseFullAddress = distanceRecord.getWarehouseProvinceCity() + ", " + distanceRecord.getWarehouseDistrict() + ", " +
                        distanceRecord.getWarehouseWard();
                warehouseAddress.setText("Address: " + warehouseFullAddress);
                route.setText("Route: " + distanceRecord.getRoute());
            } else {
                distanceTextView.setText("Failed to fetch distance data");
                Log.e(TAG, "DistanceRecord is null");
            }
        });

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistanceRecord distanceRecord = viewModel.getDistanceRecord().getValue();
                if (distanceRecord != null) {
                    try {
                        DistanceData distanceData = new DistanceData();
                        distanceData.setUserId(distanceRecord.getUserId());
                        distanceData.setOriginName(distanceRecord.getWarehouseName());  // Sử dụng tên kho làm nơi xuất phát
                        distanceData.setOriginLatitude(distanceRecord.getOriginLatitude());
                        distanceData.setOriginLongitude(distanceRecord.getOriginLongitude());
                        distanceData.setWarehouseId(distanceRecord.getWarehouseId());
                        distanceData.setDestinationName(distanceRecord.getReceiverName());  // Sử dụng tên người nhận làm nơi đến
                        distanceData.setDestinationLatitude(distanceRecord.getDestinationLatitude());
                        distanceData.setDestinationLongitude(distanceRecord.getDestinationLongitude());
                        distanceData.setDistance(distanceRecord.getDistance());
                        distanceData.setRoute(distanceRecord.getRoute());

                        PaymentRequest paymentRequest = new PaymentRequest(
                                1L,
                                userId.intValue(),
                                selectedGroupItems,
                                distanceData,  // Sử dụng DistanceData đã chuyển đổi
                                adapter.getTotalPrice(),
                                "mobile" // Truyền thêm thông tin về nền tảng
                        );

                        PaymentService paymentService = PaymentRetrofitClient.getPaymentService();
                        Call<String> call = paymentService.initiatePayment(paymentRequest);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    String responseBody = response.body();
                                    Log.d(TAG, "Response Body: " + responseBody);

                                    // Kiểm tra xem phản hồi có phải là URL không
                                    if (responseBody.startsWith("https://")) {
                                        // Mở URL trong trình duyệt
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(responseBody));
                                        startActivity(browserIntent);
                                    } else {
                                        Toast.makeText(CheckoutPage.this, "Unexpected response format", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Unexpected response: " + responseBody);
                                    }
                                } else {
                                    Log.e(TAG, "Failed to receive response. Response code: " + response.code() + ", Message: " + response.message());
                                    try {
                                        if (response.errorBody() != null) {
                                            Log.e(TAG, "Error Body: " + response.errorBody().string());
                                        }
                                    } catch (IOException e) {
                                        Log.e(TAG, "Error reading error body", e);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.e(TAG, "Request failed", t);
                            }
                        });

                    } catch (Exception e) {
                        Toast.makeText(CheckoutPage.this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "An error occurred while preparing payment request", e);
                    }
                } else {
                    Toast.makeText(CheckoutPage.this, "Failed to fetch distance data", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "DistanceRecord is null when trying to create payment request");
                }
            }
        });
    }
}
