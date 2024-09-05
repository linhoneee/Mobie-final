package com.example.brandtests.service;

import com.example.brandtests.model.ApplyCouponRequest;
import com.example.brandtests.model.CustomerCoupon;
import com.example.brandtests.model.DiscountResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CustomerCouponService {
    @GET("/customer-coupons")
    Call<List<CustomerCoupon>> getAllCustomerCoupons();

    @POST("/customer-coupons/apply")
    Call<DiscountResult> applyCoupon(@Body ApplyCouponRequest request);
}
