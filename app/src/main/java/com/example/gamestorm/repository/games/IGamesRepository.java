package com.example.gamestorm.repository.games;

import androidx.lifecycle.MutableLiveData;

import com.example.gamestorm.model.GameApiResponse;

import java.util.List;

public interface IGamesRepository {
    MutableLiveData<List<GameApiResponse>> fetchPopularGames(long lastUpdate);
    MutableLiveData<List<GameApiResponse>> fetchBestGames(long lastUpdate);
    MutableLiveData<List<GameApiResponse>> fetchLatestGames(long lastUpdate);
    MutableLiveData<List<GameApiResponse>> fetchIncomingGames(long lastUpdate);
    MutableLiveData<GameApiResponse> fetchGame(int id);
    MutableLiveData<List<GameApiResponse>> fetchExploreGames(long lastUpdate);
    MutableLiveData<List<GameApiResponse>> fetchCompanyGames(String company);
    MutableLiveData<List<GameApiResponse>> fetchFranchiseGames(String franchise);
    MutableLiveData<List<GameApiResponse>> fetchGenreGames(String genre);
    void updateWantedGame(GameApiResponse game);
    void updatePlayingGame(GameApiResponse game);
    void updatePlayedGame(GameApiResponse game);
    MutableLiveData<List<GameApiResponse>> getWantedGames(boolean isFirstLoading);
    MutableLiveData<List<GameApiResponse>> getPlayingGames(boolean isFirstLoading);
    MutableLiveData<List<GameApiResponse>> getPlayedGames(boolean isFirstLoading);
    MutableLiveData<List<GameApiResponse>> getForYouGames(long lastUpdate);
    MutableLiveData<List<GameApiResponse>> getSearchedGames(String userInput);
    MutableLiveData<List<GameApiResponse>> getSimilarGames(List<Integer> similarGames);
    MutableLiveData<List<GameApiResponse>> getAllPopularGames();
    MutableLiveData<List<GameApiResponse>> getAllBestGames();
    MutableLiveData<List<GameApiResponse>> getAllLatestGames();
    MutableLiveData<List<GameApiResponse>> getAllIncomingGames();
    MutableLiveData<List<GameApiResponse>> getSearchedGames(String genre, String platform, String year);
}
