package com.example.brandtests.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PaymentRetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8009/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())  // Thêm converter để xử lý phản hồi chuỗi
                    .addConverterFactory(GsonConverterFactory.create())  // Giữ lại Gson cho các API khác nếu cần
                    .build();
        }
        return retrofit;
    }

    public static PaymentService getPaymentService() {
        return getClient().create(PaymentService.class);
    }
}
