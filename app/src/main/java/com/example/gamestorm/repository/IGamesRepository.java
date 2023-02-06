package com.example.gamestorm.repository;

public interface IGamesRepository {
    void fetchGames(String query, long lastUpdate,int count);
}
