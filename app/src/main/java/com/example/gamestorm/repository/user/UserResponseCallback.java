package com.example.gamestorm.repository.user;

import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.model.User;

import java.util.List;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
    void onSuccessFromRemoteDatabase(List<GameApiResponse> gameApiResponses, String i);
    void onFailureFromRemoteDatabase(String message);
    void onSuccessLogout();
}
