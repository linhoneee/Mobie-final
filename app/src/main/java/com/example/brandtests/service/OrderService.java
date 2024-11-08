package com.example.brandtests.service;

import com.example.brandtests.model.Address;
import com.example.brandtests.model.Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OrderService {

    @GET("/orders/{userId}")
    Call<List<Order>> getOrderByOrderId(@Path("userId") Long userId);
    }

