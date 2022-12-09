package com.example.gamestorm.Repository;

import com.example.gamestorm.Model.Game;

public interface IGamesRepository {
    enum JsonParserType {
        JSON_READER,
        JSON_OBJECT_ARRAY,
        GSON,
        JSON_ERROR
    }

    void fetchGames(long lastUpdate);

    void updateGames(Game game);

    void getFavoriteGames();

    void deleteFavoriteGames();
}
