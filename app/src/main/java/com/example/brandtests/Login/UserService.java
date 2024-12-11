package com.example.brandtests.Login;


import com.example.brandtests.model.Address;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

    @POST("/users/login")
    Call<Map<String, String>> loginUser(@Body User loginRequest);

    @GET("/users/SSO/signingoogle")
    Call<Map<String, String>> loginWithGoogle();

}