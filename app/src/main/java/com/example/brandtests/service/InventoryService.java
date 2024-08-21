package com.example.brandtests.service;

import com.example.brandtests.model.Inventory;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface InventoryService {

    @GET("/inventory")
    Call<List<Inventory>> getAllInventories();
}
