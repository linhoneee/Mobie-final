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

    @GET("/distance/calculateWithFullAddress")
    Call<DistanceRecord> calculateDistanceWithFullAddress(
            @Query("receiverName") String receiverName,
            @Query("street") String street,
            @Query("ward") String ward,
            @Query("district") String district,
            @Query("provinceCity") String provinceCity,
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude,
            @Query("warehouseIds") List<Long> warehouseIds
    );
}
