package com.example.brandtests.service;



import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DistanceRetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://10.0.2.2:8008";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static DistanceService getDistanceService() {
        return getRetrofitInstance().create(DistanceService.class);
    }
    public static ShippingService getShippingService() {
        return getRetrofitInstance().create(ShippingService.class);
    }
}
