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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.brandtests.Login.RetrofitClient;
import com.example.brandtests.R;
import com.example.brandtests.adapter.CheckoutAdapter;
import com.example.brandtests.model.Address;
import com.example.brandtests.model.DistanceData;
import com.example.brandtests.model.DistanceRecord;
import com.example.brandtests.model.Item;
import com.example.brandtests.model.PaymentRequest;
import com.example.brandtests.service.AddressService;
import com.example.brandtests.service.DistanceRetrofitClient;
import com.example.brandtests.service.DistanceService;
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

    private TextView primaryAddressTextView;
    private Button changeAddressButton;
    private String selectedWarehouseId;
    private List<Item> selectedGroupItems;

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

        // Thêm TextView và Button cho địa chỉ chính
        primaryAddressTextView = findViewById(R.id.primaryAddressTextView);
        changeAddressButton = findViewById(R.id.changeAddressButton);

        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong("UserID", -1);
        Log.d(TAG, "Retrieved UserID from SharedPreferences: " + userId);

        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        selectedWarehouseId = getIntent().getStringExtra("selectedWarehouseId");
        selectedGroupItems = getIntent().getParcelableArrayListExtra("selectedGroupItems");
        CheckoutAdapter adapter = new CheckoutAdapter(this, selectedGroupItems);
        selectedItemsListView.setAdapter(adapter);
        warehouseIdTextView.setText("Warehouse ID: " + selectedWarehouseId);
        totalTextView.setText("Tổng tiền: $" + String.format("%.2f", adapter.getTotalPrice()));

        // Tính toán khoảng cách ban đầu dựa trên địa chỉ chính
        viewModel.calculateDistance(userId, Arrays.asList(Long.parseLong(selectedWarehouseId)));

        viewModel.getDistanceRecord().observe(this, distanceRecord -> {
            if (distanceRecord != null) {
                updateUIWithDistanceRecord(distanceRecord);
            } else {
                distanceTextView.setText("Failed to fetch distance data");
                Log.e(TAG, "DistanceRecord is null");
            }
        });

        loadPrimaryAddress(); // Gọi phương thức để tải địa chỉ chính

        changeAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressSelectionDialog(); // Mở dialog để chọn địa chỉ mới
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

    private void loadPrimaryAddress() {
        AddressService service = RetrofitClient.getAddressService();
        Call<Address> call = service.getPrimaryAddress(userId);
        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Address primaryAddress = response.body();
                    primaryAddressTextView.setText("Primary Address: " + primaryAddress.getFullAddress());
                    // Tính toán lại khoảng cách với địa chỉ chính ban đầu
                    calculateDistanceWithFullAddress(primaryAddress);
                } else {
                    primaryAddressTextView.setText("Primary Address: Not found");
                    Log.e(TAG, "Failed to load primary address. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                primaryAddressTextView.setText("Primary Address: Error loading");
                Log.e(TAG, "Error fetching primary address", t);
            }
        });
    }

    private void showAddressSelectionDialog() {
        AddressService service = RetrofitClient.getAddressService();
        Call<List<Address>> call = service.getAddressesByUserId(userId);
        call.enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(Call<List<Address>> call, Response<List<Address>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Address> addresses = response.body();
                    String[] addressArray = new String[addresses.size()];
                    for (int i = 0; i < addresses.size(); i++) {
                        addressArray[i] = addresses.get(i).getFullAddress();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutPage.this);
                    builder.setTitle("Select Address")
                            .setItems(addressArray, (dialog, which) -> {
                                Address selectedAddress = addresses.get(which);
                                primaryAddressTextView.setText("Primary Address: " + selectedAddress.getFullAddress());
                                // Tính toán lại khoảng cách với địa chỉ được chọn
                                calculateDistanceWithFullAddress(selectedAddress);
                            })
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                } else {
                    Toast.makeText(CheckoutPage.this, "Failed to load addresses", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to load addresses. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Address>> call, Throwable t) {
                Toast.makeText(CheckoutPage.this, "Error loading addresses", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error fetching addresses", t);
            }
        });
    }

    private void calculateDistanceWithFullAddress(Address address) {
        DistanceService distanceService = DistanceRetrofitClient.getDistanceService();

        // Kiểm tra và log lại các giá trị đang được truyền vào API để đảm bảo tính chính xác
        Log.d(TAG, "Receiver Name: " + address.getReceiverName());
        Log.d(TAG, "Street: " + address.getStreet());
        Log.d(TAG, "Ward: " + address.getWard());
        Log.d(TAG, "District: " + address.getDistrict());
        Log.d(TAG, "Province City: " + address.getProvinceCity());
        Log.d(TAG, "Latitude: " + address.getLatitude());
        Log.d(TAG, "Longitude: " + address.getLongitude());
        Log.d(TAG, "Warehouse IDs: " + Arrays.asList(Long.parseLong(selectedWarehouseId)));

        Call<DistanceRecord> call = distanceService.calculateDistanceWithFullAddress(
                address.getReceiverName(),
                address.getStreet(),
                address.getWard(),
                address.getDistrict(),
                address.getProvinceCity(),
                address.getLatitude(),
                address.getLongitude(),
                Arrays.asList(Long.parseLong(selectedWarehouseId))
        );

        call.enqueue(new Callback<DistanceRecord>() {
            @Override
            public void onResponse(Call<DistanceRecord> call, Response<DistanceRecord> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DistanceRecord newDistanceRecord = response.body();
                    updateUIWithDistanceRecord(newDistanceRecord);
                } else {
                    Toast.makeText(CheckoutPage.this, "Failed to calculate distance", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to calculate distance. Response code: " + response.code());
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
            public void onFailure(Call<DistanceRecord> call, Throwable t) {
                Toast.makeText(CheckoutPage.this, "Error calculating distance", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error calculating distance", t);
            }
        });
    }



    private void updateUIWithDistanceRecord(DistanceRecord distanceRecord) {
        TextView tvReceiverName = findViewById(R.id.tvReceiverName);
        TextView tvFullAddress = findViewById(R.id.tvFullAddress);
        TextView tvWarehouseName = findViewById(R.id.tvWarehouseName);
        TextView distanceTextView = findViewById(R.id.distanceTextView);
        TextView warehouseAddress = findViewById(R.id.warehouseAddress);
        TextView route = findViewById(R.id.route);

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
    }
}
