package com.example.brandtests.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.brandtests.Login.UserService;
import com.example.brandtests.model.Address;
import com.example.brandtests.model.Order;
import com.example.brandtests.model.User;
import com.example.brandtests.service.AddressService;
import com.example.brandtests.service.OrderRetrofitClient;
import com.example.brandtests.service.OrderService;
import com.example.brandtests.Login.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<User> userData = new MutableLiveData<>();
    private final MutableLiveData<List<Address>> addressData = new MutableLiveData<>();
    private final MutableLiveData<List<Order>> orderHistoryData = new MutableLiveData<>();

    private final AddressService addressService;
    private final OrderService orderService;

    private static final String TAG = "ProfileViewModel";

    public ProfileViewModel(Long userId) {
        addressService = RetrofitClient.getClient().create(AddressService.class);
        orderService = OrderRetrofitClient.getClient().create(OrderService.class);

        loadUserData(userId);
        loadAddressData(userId);
        loadOrderHistoryData(userId);
    }

    public LiveData<User> getUserData() {
        return userData;
    }

    public LiveData<List<Address>> getAddressData() {
        return addressData;
    }

    public LiveData<List<Order>> getOrderHistoryData() {
        return orderHistoryData;
    }

    private void loadUserData(Long userId) {
        String userUrl = RetrofitClient.getClient().baseUrl() + "users/" + userId;
        Log.d(TAG, "Calling User API: " + userUrl);

        addressService.getUserByUserId(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userData.setValue(response.body());
                    Log.d(TAG, "User data loaded: " + response.body());
                } else {
                    try {
                        // Log chi tiết dữ liệu JSON khi xảy ra lỗi
                        Log.e(TAG, "User data response unsuccessful. Code: " + response.code() + ", Body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Failed to load user data: " + t.getMessage(), t);
            }
        });
    }

    private void loadAddressData(Long userId) {
        String addressUrl = RetrofitClient.getClient().baseUrl() + "addresses/user/" + userId;
        Log.d(TAG, "Calling Address API: " + addressUrl);

        addressService.getAddressesByUserId(userId).enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(Call<List<Address>> call, Response<List<Address>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addressData.setValue(response.body());
                    Log.d(TAG, "Address data loaded: " + response.body());
                } else {
                    try {
                        // Log chi tiết dữ liệu JSON khi xảy ra lỗi
                        Log.e(TAG, "Address data response unsuccessful. Code: " + response.code() + ", Body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Address>> call, Throwable t) {
                Log.e(TAG, "Failed to load address data: " + t.getMessage(), t);
            }
        });
    }

    private void loadOrderHistoryData(Long userId) {
        String orderUrl = RetrofitClient.getClient().baseUrl() + "orders/" + userId;
        Log.d(TAG, "Calling Order API: " + orderUrl);

        orderService.getOrderByOrderId(userId).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderHistoryData.setValue(response.body());
                    Log.d(TAG, "Order data loaded: " + response.body().toString());
                } else {
                    try {
                        // Log chi tiết dữ liệu JSON khi xảy ra lỗi
                        Log.e(TAG, "Order data response unsuccessful. Code: " + response.code() + ", Body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.e(TAG, "Failed to load order data: " + t.getMessage(), t);
            }
        });
    }




    public static class Factory implements ViewModelProvider.Factory {
        private final Long userId;

        public Factory(Long userId) {
            this.userId = userId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
                return (T) new ProfileViewModel(userId);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
