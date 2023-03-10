package com.example.gamestorm.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.example.gamestorm.model.User;

import java.util.Set;

public interface IUserRepository {
    MutableLiveData<User> getUser(String name, String email, String password, boolean isUserRegistered);
    MutableLiveData<User> getGoogleUser(String idToken);
    MutableLiveData<User> getUserWantedGames(String idToken);
    MutableLiveData<User> getUserPlayingGames(String idToken);
    MutableLiveData<User> getUserPlayedGames(String idToken);
    MutableLiveData<User> logout();
    User getLoggedUser();
    void signUp(String name, String email, String password);
    void signIn(String email, String password);
    void signInWithGoogle(String token);
    void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken);

}
