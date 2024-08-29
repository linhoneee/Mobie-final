package com.example.brandtests.service;

import com.example.brandtests.model.PaymentRequest;
import com.squareup.picasso.Request;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PaymentService {
    @POST("/paypal")
    Call<String> initiatePayment(@Body PaymentRequest request);
}
