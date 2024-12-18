package com.example.brandtests.Login;

import com.example.brandtests.service.AddressService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/";
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

    public static UserService getUserService() {
        return getClient().create(UserService.class);
    }

    public static AddressService getAddressService() {
        return getClient().create(AddressService.class);
    }

}