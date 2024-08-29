package com.example.brandtests.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.brandtests.model.DistanceRecord;
import com.example.brandtests.model.Item;
import com.example.brandtests.service.DistanceRetrofitClient;
import com.example.brandtests.service.DistanceService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutViewModel extends ViewModel {

    private static final String TAG = "CheckoutViewModel";
    private MutableLiveData<Map<String, List<Item>>> warehouseGroups = new MutableLiveData<>();
    private MutableLiveData<String> selectedWarehouseId = new MutableLiveData<>();

    public LiveData<Map<String, List<Item>>> getWarehouseGroups() {
        return warehouseGroups;
    }

    private MutableLiveData<DistanceRecord> distanceRecord = new MutableLiveData<>();

    public LiveData<DistanceRecord> getDistanceRecord() {
        return distanceRecord;
    }

    public LiveData<String> getSelectedWarehouseId() {
        return selectedWarehouseId;
    }

    public void setSelectedWarehouseId(String warehouseId) {
        selectedWarehouseId.setValue(warehouseId);
    }

    public void groupItemsByWarehouse(List<Item> selectedItems, Map<Long, String> itemWarehouseIdsMap) {
        Map<String, List<Item>> groupedItems = new HashMap<>();

        for (Item item : selectedItems) {
            String warehouseIds = itemWarehouseIdsMap.get(item.getProductId());
            Log.d(TAG, "Processing item: " + item.getName() + ", Warehouse IDs: " + warehouseIds);
            if (warehouseIds != null) {
                String[] warehouseIdArray = warehouseIds.split(",");
                for (String warehouseId : warehouseIdArray) {
                    groupedItems.putIfAbsent(warehouseId, new ArrayList<>());
                    groupedItems.get(warehouseId).add(item);
                    Log.d(TAG, "Item added to warehouse group: " + warehouseId);
                }
            } else {
                Log.e(TAG, "Warehouse IDs is null for item: " + item.getName());
            }
        }

        warehouseGroups.setValue(groupedItems);
        Log.d(TAG, "Grouped items by warehouse: " + groupedItems.size());
    }

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
