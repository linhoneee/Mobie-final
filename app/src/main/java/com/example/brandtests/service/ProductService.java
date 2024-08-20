package com.example.brandtests.service;

import com.example.brandtests.model.ProductDTOuser;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ProductService {

    @GET("/products/user")
    Call<List<ProductDTOuser>> getAllProductsByUser();
}
