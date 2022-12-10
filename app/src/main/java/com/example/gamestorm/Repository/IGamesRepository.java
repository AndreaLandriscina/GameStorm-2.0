package com.example.gamestorm.Repository;

import com.example.gamestorm.Model.GameApiResponse;

public interface IGamesRepository {
    enum JsonParserType {
        JSON_READER,
        JSON_OBJECT_ARRAY,
        GSON,
        JSON_ERROR
    }

    void fetchGames(String query, long lastUpdate);

    void updateGames(GameApiResponse game);

    void getFavoriteGames();

    void deleteFavoriteGames();
}
