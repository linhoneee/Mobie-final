package com.example.brandtests.Login;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class UserViewModelFactory implements ViewModelProvider.Factory {

    private UserService userService;

    public UserViewModelFactory(UserService userService) {
        this.userService = userService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(userService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
