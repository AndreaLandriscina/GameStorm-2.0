package com.example.gamestorm.ui.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gamestorm.model.User;
import com.example.gamestorm.repository.user.IUserRepository;

public class UserViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();

    private final IUserRepository userRepository;
    private MutableLiveData<User> userMutableLiveData;
    private MutableLiveData<User> userWantedGamesMutableLiveData;
    private MutableLiveData<User> userPlayingGamesMutableLiveData;
    private MutableLiveData<User> userPlayedGamesMutableLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
    }

    public MutableLiveData<User> getUserMutableLiveData(String name, String email, String password, boolean isUserRegistered) {
        if (userMutableLiveData == null) {
            getUserData(name, email, password, isUserRegistered);
        }
        return userMutableLiveData;
    }


    public MutableLiveData<User> getGoogleUserMutableLiveData(String token) {
        if (userMutableLiveData == null) {
            getUserData(token);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<User> getUserWantedGamesMutableLiveData(String idToken) {
        if (userWantedGamesMutableLiveData == null) {
            getUserWantedGames(idToken);
        }
        return userWantedGamesMutableLiveData;
    }
    public MutableLiveData<User> getUserPlayingGamesMutableLiveData(String idToken) {
        if (userPlayingGamesMutableLiveData == null) {
            getUserPlayingGames(idToken);
        }
        return userPlayingGamesMutableLiveData;
    }
    public MutableLiveData<User> getUserPlayedGamesMutableLiveData(String idToken) {
        if (userPlayedGamesMutableLiveData == null) {
            getUserPlayedGames(idToken);
        }
        return userPlayedGamesMutableLiveData;
    }

    private void getUserWantedGames(String idToken) {
        userWantedGamesMutableLiveData = userRepository.getUserWantedGames(idToken);
    }
    private void getUserPlayingGames(String idToken) {
        userPlayingGamesMutableLiveData = userRepository.getUserPlayingGames(idToken);
    }
    private void getUserPlayedGames(String idToken) {
        userPlayedGamesMutableLiveData = userRepository.getUserPlayedGames(idToken);
    }

    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    public void logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }

    }

    public void getUser(String name, String email, String password, boolean isUserRegistered) {
        userRepository.getUser(name, email, password, isUserRegistered);
    }

    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    private void getUserData(String name, String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(name, email, password, isUserRegistered);
    }

    private void getUserData(String token) {
        userMutableLiveData = userRepository.getGoogleUser(token);
    }

}
