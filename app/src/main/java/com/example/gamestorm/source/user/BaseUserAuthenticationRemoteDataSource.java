package com.example.gamestorm.source.user;

import com.example.gamestorm.model.User;
import com.example.gamestorm.repository.user.UserResponseCallback;

public abstract class BaseUserAuthenticationRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }
    public abstract User getLoggedUser();
    public abstract void logout();
    public abstract void signUp(String name, String email, String password);
    public abstract void signIn(String email, String password);
    public abstract void signInWithGoogle(String idToken);
}
