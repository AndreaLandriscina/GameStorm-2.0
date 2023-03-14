package com.example.gamestorm.repository.games;

import com.example.gamestorm.model.GameApiResponse;

import java.util.List;

public interface ResponseCallback {
    void onSuccess(List<GameApiResponse> gamesList, int count);
    void onFailure(String errorMessage);
}