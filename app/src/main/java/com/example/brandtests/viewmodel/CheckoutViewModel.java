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
    private MutableLiveData<DistanceRecord> distanceRecord = new MutableLiveData<>();


    public LiveData<DistanceRecord> getDistanceRecord() {
        return distanceRecord;
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
}
