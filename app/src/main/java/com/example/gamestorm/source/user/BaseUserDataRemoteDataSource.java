package com.example.gamestorm.source.user;

import com.example.gamestorm.model.User;
import com.example.gamestorm.repository.user.UserResponseCallback;

import java.util.Set;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);
    public abstract void getUserWantedGames(String idToken);
    public abstract void getUserPlayingGames(String idToken);
    public abstract void getUserPlayedGames(String idToken);
    public abstract void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken);
}
