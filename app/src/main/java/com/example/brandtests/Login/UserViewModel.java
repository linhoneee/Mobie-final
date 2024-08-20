package com.example.brandtests.Login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends ViewModel {

    private UserService userService;
    private MutableLiveData<Map<String, String>> loginResponse;

    public UserViewModel(UserService userService) {
        this.userService = userService;
        this.loginResponse = new MutableLiveData<>();
    }

    public LiveData<Map<String, String>> getLoginResponse() {
        return loginResponse;
    }

    public void loginUser(User user) {
        userService.loginUser(user).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loginResponse.setValue(response.body());
                } else {
                    loginResponse.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                loginResponse.setValue(null);
            }
        });
    }
}
