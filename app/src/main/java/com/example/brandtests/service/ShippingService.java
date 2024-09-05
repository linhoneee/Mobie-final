package com.example.brandtests.service;

import com.example.brandtests.model.Address;
import com.example.brandtests.model.Shipping;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ShippingService {
    @GET("/shipping-types")
    Call<List<Shipping>> getAllShippingTypes();
}
