package com.example.brandtests.service;

import com.example.brandtests.model.DistanceRecord;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DistanceService {
    @GET("/distance/calculate")
    Call<DistanceRecord> calculateNearestWarehouse(
            @Query("userId") Long userId,
            @Query("warehouseIds") List<Long> warehouseIds
    );
}
