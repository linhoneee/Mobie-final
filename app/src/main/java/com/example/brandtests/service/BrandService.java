package com.example.brandtests.service;

import com.example.brandtests.model.Brand;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BrandService {


    @GET("/brands/{id}")
    Call<Brand> getBrandById(@Path("id") Long id);

    @POST("/brands")
    Call<Brand> createBrand(@Body Brand brand);

    @PUT("/brands/{id}")
    Call<Brand> updateBrand(@Path("id") Long id, @Body Brand brand);

    @DELETE("/brands/{id}")
    Call<Void> deleteBrand(@Path("id") Long id);

    @GET("/brands")
    Call<List<Brand>> getBrands();
}
