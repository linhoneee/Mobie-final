package com.example.brandtests.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.brandtests.model.DistanceRecord;
import com.example.brandtests.model.Item;
import com.example.brandtests.model.ApplyCouponRequest;
import com.example.brandtests.model.DiscountResult;
import com.example.brandtests.model.Shipping;
import com.example.brandtests.service.CustomerCouponRetrofitClient;
import com.example.brandtests.service.CustomerCouponService;
import com.example.brandtests.service.DistanceRetrofitClient;
import com.example.brandtests.service.DistanceService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutViewModel extends ViewModel {

    private static final String TAG = "CheckoutViewModel";
    private MutableLiveData<Map<String, List<Item>>> warehouseGroups = new MutableLiveData<>();
    private MutableLiveData<DistanceRecord> distanceRecord = new MutableLiveData<>();
    private MutableLiveData<Double> shippingCost = new MutableLiveData<>();
    private MutableLiveData<Double> totalCost = new MutableLiveData<>();
    private MutableLiveData<Double> productTotal = new MutableLiveData<>();
    private MutableLiveData<DiscountResult> discountResult = new MutableLiveData<>();

    public LiveData<DistanceRecord> getDistanceRecord() {
        return distanceRecord;
    }

    public LiveData<Double> getShippingCost() {
        return shippingCost;
    }

    public LiveData<Double> getTotalCost() {
        return totalCost;
    }

    public LiveData<Double> getProductTotal() {
        return productTotal;
    }

    public LiveData<DiscountResult> getDiscountResult() {
        return discountResult;
    }

    // Tính toán khoảng cách và cập nhật DistanceRecord
    public void calculateDistance(Long userId, List<Long> warehouseIds) {
        DistanceService service = DistanceRetrofitClient.getRetrofitInstance().create(DistanceService.class);
        Call<DistanceRecord> call = service.calculateNearestWarehouse(userId, warehouseIds);
        call.enqueue(new Callback<DistanceRecord>() {
            @Override
            public void onResponse(Call<DistanceRecord> call, Response<DistanceRecord> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Distance fetched successfully: " + response.body().getRoute());
                    distanceRecord.postValue(response.body());
                } else {
                    Log.e(TAG, "Error fetching distance: Response unsuccessful or empty, Code: " + response.code());
                    distanceRecord.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<DistanceRecord> call, Throwable t) {
                Log.e(TAG, "Error fetching distance: API call failed", t);
                distanceRecord.postValue(null);
            }
        });
    }

    // Tính toán chi phí vận chuyển và tổng chi phí đơn hàng
    public void calculateShippingCost(Shipping selectedShipping, DistanceRecord distanceRecord, double totalWeight) {
        double distance = distanceRecord != null ? distanceRecord.getDistance() : 0.0;
        double shippingCostValue = (selectedShipping.getPricePerKm() * distance) + (selectedShipping.getPricePerKg() * totalWeight);
        shippingCost.setValue(shippingCostValue);
        calculateTotalCost();
    }

    public void setProductTotal(double total) {
        productTotal.setValue(total);
        calculateTotalCost();
    }

    private void calculateTotalCost() {
        double total = (productTotal.getValue() != null ? productTotal.getValue() : 0.0) +
                (shippingCost.getValue() != null ? shippingCost.getValue() : 0.0);
        totalCost.setValue(total);
    }

    // Áp dụng mã giảm giá
    public void applyCoupon(String couponCode, double productTotal, double shippingCost) {
        ApplyCouponRequest request = new ApplyCouponRequest();
        request.setCode(couponCode);
        request.setOrderValue(productTotal);
        request.setShippingCost(shippingCost);

        CustomerCouponService service = CustomerCouponRetrofitClient.getCustomerCouponService();
        Call<DiscountResult> call = service.applyCoupon(request);
        call.enqueue(new Callback<DiscountResult>() {
            @Override
            public void onResponse(Call<DiscountResult> call, Response<DiscountResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    discountResult.setValue(response.body());
                } else {
                    discountResult.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<DiscountResult> call, Throwable t) {
                discountResult.setValue(null);
            }
        });
    }
}
