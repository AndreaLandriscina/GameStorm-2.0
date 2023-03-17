package com.example.gamestorm.ui.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.gamestorm.repository.user.IUserRepository;
import com.example.gamestorm.ui.viewModel.UserViewModel;


/**
 * Custom ViewModelProvider to be able to have a custom constructor
 * for the UserViewModel class.
 */
public class UserViewModelFactory implements ViewModelProvider.Factory {

    private final IUserRepository userRepository;

    public UserViewModelFactory(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new UserViewModel(userRepository);
    }
}
