package com.example.gamestorm.repository;

import com.example.gamestorm.model.GameApiResponse;

public interface IGamesRepository {
    enum JsonParserType {
        JSON_READER,
        JSON_OBJECT_ARRAY,
        GSON,
        JSON_ERROR
    }

    void fetchGames(String query, long lastUpdate,int count);

    void updateGames(GameApiResponse game);

    void getFavoriteGames();

    void deleteFavoriteGames();
}
