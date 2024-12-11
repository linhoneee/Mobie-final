package com.example.brandtests.service;

import com.example.brandtests.model.Cart;
import com.example.brandtests.model.Item;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartService {

    @POST("/cart/{userId}")
    Call<Cart> addItemToCart(@Path("userId") Long userId, @Body Item item);



    @PUT("/cart/{userId}")
    Call<Cart> removeItemFromCart(@Path("userId") Long userId, @Body Item item);


    @GET("/cart/{userId}")
    Call<Cart> getCart(@Path("userId") Long userId);

    @DELETE("cart/{userId}")
    Call<Cart> clearItemFromCart(@Path("userId") Long userId, @Body Item item);


}
