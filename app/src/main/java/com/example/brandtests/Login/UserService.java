package com.example.brandtests.Login;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

    @POST("/users/login")
    Call<Map<String, String>> loginUser(@Body User loginRequest);
}