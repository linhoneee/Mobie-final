package com.example.brandtests.service;


import com.example.brandtests.model.Address;
import com.example.brandtests.model.Brand;
import com.example.brandtests.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AddressService {
    @GET("/addresses/primary/{userId}")
    Call<Address> getPrimaryAddress(@Path("userId") Long userId);

    @GET("/addresses/user/{userId}")
    Call<List<Address>> getAddressesByUserId(@Path("userId") Long userId);

    @GET("/users/{userId}")
    Call<User> getUserByUserId(@Path("userId") Long userId);

}