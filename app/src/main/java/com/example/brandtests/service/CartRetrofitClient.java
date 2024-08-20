package com.example.brandtests.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CartRetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2:6004/"; // URL của API cho Cart
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static CartService getCartService() {
        return getClient().create(CartService.class);
    }
}
