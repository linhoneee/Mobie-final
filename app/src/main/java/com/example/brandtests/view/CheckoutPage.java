package com.example.brandtests.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.brandtests.Login.RetrofitClient;
import com.example.brandtests.R;
import com.example.brandtests.adapter.CheckoutAdapter;
import com.example.brandtests.model.Address;
import com.example.brandtests.model.ApplyCouponRequest;
import com.example.brandtests.model.CustomerCoupon;
import com.example.brandtests.model.DistanceData;
import com.example.brandtests.model.DistanceRecord;
import com.example.brandtests.model.DiscountResult;
import com.example.brandtests.model.Item;
import com.example.brandtests.model.PaymentRequest;
import com.example.brandtests.model.Shipping;
import com.example.brandtests.service.AddressService;
import com.example.brandtests.service.CustomerCouponRetrofitClient;
import com.example.brandtests.service.CustomerCouponService;
import com.example.brandtests.service.DistanceRetrofitClient;
import com.example.brandtests.service.DistanceService;
import com.example.brandtests.service.PaymentService;
import com.example.brandtests.service.PaymentRetrofitClient;
import com.example.brandtests.service.ShippingService;
import com.example.brandtests.viewmodel.CheckoutViewModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
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
    private Spinner couponSpinner;
    private String selectedCouponCode;
    private TextView discountedTotalTextView;
    private TextView totalTextView;
    private TextView shippingCostTextView;
    private TextView totalCostTextView;
    private Button applyCouponButton;
    private RadioGroup shippingRadioGroup;
    private Shipping selectedShipping;
    private double shippingCost;
    private CheckoutAdapter adapter; // Khai báo biến adapter ở cấp lớp

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
        totalTextView = findViewById(R.id.totalTextView);
        shippingCostTextView = findViewById(R.id.shippingCostTextView);
        totalCostTextView = findViewById(R.id.totalCostTextView);
        shippingRadioGroup = findViewById(R.id.shippingRadioGroup);
        Button checkoutButton = findViewById(R.id.checkoutButton);
        applyCouponButton = findViewById(R.id.applyCouponButton);

        // Thêm TextView và Button cho địa chỉ chính
        primaryAddressTextView = findViewById(R.id.primaryAddressTextView);
        changeAddressButton = findViewById(R.id.changeAddressButton);

        // Thêm Spinner và TextView cho mã giảm giá
        couponSpinner = findViewById(R.id.couponSpinner);
        discountedTotalTextView = findViewById(R.id.discountedTotalTextView);

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
        adapter = new CheckoutAdapter(this, selectedGroupItems); // Khởi tạo adapter
        selectedItemsListView.setAdapter(adapter);
        warehouseIdTextView.setText("Warehouse ID: " + selectedWarehouseId);
        totalTextView.setText("Product Total: $" + String.format("%.2f", adapter.getTotalPrice()));

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
        loadCoupons(); // Gọi phương thức để tải các mã giảm giá
        loadShippingTypes(); // Gọi phương thức để tải các loại vận chuyển

        changeAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressSelectionDialog(); // Mở dialog để chọn địa chỉ mới
            }
        });

        applyCouponButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCouponCode = couponSpinner.getSelectedItem().toString();
                applyCoupon(); // Áp dụng mã giảm giá
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
                        distanceData.setOriginName(distanceRecord.getWarehouseName());
                        distanceData.setOriginLatitude(distanceRecord.getOriginLatitude());
                        distanceData.setOriginLongitude(distanceRecord.getOriginLongitude());
                        distanceData.setWarehouseId(distanceRecord.getWarehouseId());
                        distanceData.setDestinationName(distanceRecord.getReceiverName());
                        distanceData.setDestinationLatitude(distanceRecord.getDestinationLatitude());
                        distanceData.setDestinationLongitude(distanceRecord.getDestinationLongitude());
                        distanceData.setDistance(distanceRecord.getDistance());
                        distanceData.setRoute(distanceRecord.getRoute());

                        PaymentRequest paymentRequest = new PaymentRequest(
                                1L,
                                userId.intValue(),
                                selectedGroupItems,
                                distanceData,
                                adapter.getTotalPrice(),
                                "mobile"
                        );

                        PaymentService paymentService = PaymentRetrofitClient.getPaymentService();
                        Call<String> call = paymentService.initiatePayment(paymentRequest);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    String responseBody = response.body();
                                    Log.d(TAG, "Response Body: " + responseBody);

                                    if (responseBody.startsWith("https://")) {
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

        // Thiết lập sự kiện khi chọn item trong Spinner
        couponSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (!selected.equals("Select a coupon")) {
                    applyCouponButton.setEnabled(true);
                } else {
                    applyCouponButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                applyCouponButton.setEnabled(false);
            }
        });

        // Thiết lập sự kiện khi chọn shipping type
        shippingRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            selectedShipping = (Shipping) selectedRadioButton.getTag();
            calculateShippingCost(); // Tính toán lại chi phí vận chuyển
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

    private void applyCoupon() {
        CustomerCouponService service = CustomerCouponRetrofitClient.getCustomerCouponService();
        ApplyCouponRequest request = new ApplyCouponRequest();
        request.setCode(selectedCouponCode);

        double orderValue = Double.parseDouble(totalTextView.getText().toString().replace("Product Total: $", ""));
        request.setOrderValue(orderValue);

        request.setShippingCost(shippingCost);

        Call<DiscountResult> call = service.applyCoupon(request);
        call.enqueue(new Callback<DiscountResult>() {
            @Override
            public void onResponse(Call<DiscountResult> call, Response<DiscountResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DiscountResult discountResult = response.body();
                    discountedTotalTextView.setText("Discounted Total: $" + discountResult.getDiscountedOrderValue());
                } else {
                    Log.e(TAG, "Failed to apply coupon. Response code: " + response.code());
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
            public void onFailure(Call<DiscountResult> call, Throwable t) {
                Log.e(TAG, "Error applying coupon", t);
            }
        });
    }

    private void loadCoupons() {
        CustomerCouponService service = CustomerCouponRetrofitClient.getCustomerCouponService();
        Call<List<CustomerCoupon>> call = service.getAllCustomerCoupons();
        call.enqueue(new Callback<List<CustomerCoupon>>() {
            @Override
            public void onResponse(Call<List<CustomerCoupon>> call, Response<List<CustomerCoupon>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CustomerCoupon> coupons = response.body();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CheckoutPage.this, android.R.layout.simple_spinner_item);
                    adapter.add("Select a coupon");
                    for (CustomerCoupon coupon : coupons) {
                        adapter.add(coupon.getCode());
                    }
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    couponSpinner.setAdapter(adapter);
                    applyCouponButton.setEnabled(false);
                } else {
                    Log.e(TAG, "Failed to load coupons. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<CustomerCoupon>> call, Throwable t) {
                Log.e(TAG, "Error loading coupons", t);
            }
        });
    }

    private void loadShippingTypes() {
        ShippingService service = DistanceRetrofitClient.getShippingService();
        Call<List<Shipping>> call = service.getAllShippingTypes();
        call.enqueue(new Callback<List<Shipping>>() {
            @Override
            public void onResponse(Call<List<Shipping>> call, Response<List<Shipping>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Shipping> shippingTypes = response.body();
                    if (!shippingTypes.isEmpty()) {
                        shippingTypes.sort(Comparator.comparingLong(Shipping::getId));
                        selectedShipping = shippingTypes.get(0);

                        for (Shipping shipping : shippingTypes) {
                            RadioButton radioButton = new RadioButton(CheckoutPage.this);
                            radioButton.setText(shipping.getName());
                            radioButton.setTag(shipping);
                            shippingRadioGroup.addView(radioButton);

                            if (shipping.equals(selectedShipping)) {
                                radioButton.setChecked(true);
                            }
                        }
                        calculateShippingCost();
                    }
                } else {
                    Log.e(TAG, "Failed to load shipping types. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Shipping>> call, Throwable t) {
                Log.e(TAG, "Error loading shipping types", t);
            }
        });
    }

    private void calculateShippingCost() {
        DistanceRecord distanceRecord = viewModel.getDistanceRecord().getValue();
        double distance = distanceRecord != null ? distanceRecord.getDistance() : 0.0;
        double totalWeight = adapter.getTotalWeight(); // Sử dụng biến adapter đã được khai báo

        if (selectedShipping != null) {
            shippingCost = (selectedShipping.getPricePerKm() * distance) + (selectedShipping.getPricePerKg() * totalWeight);
            shippingCostTextView.setText("Shipping Total: $" + String.format("%.2f", shippingCost));

            double productTotal = Double.parseDouble(totalTextView.getText().toString().replace("Product Total: $", ""));
            double totalCost = productTotal + shippingCost;
            totalCostTextView.setText("Total Cost: $" + String.format("%.2f", totalCost));
        }
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

        calculateShippingCost(); // Tính toán chi phí vận chuyển sau khi cập nhật khoảng cách
    }
}
