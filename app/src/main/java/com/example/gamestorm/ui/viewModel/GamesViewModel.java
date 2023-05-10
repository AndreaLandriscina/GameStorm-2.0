package com.example.gamestorm.ui.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.repository.games.IGamesRepository;

import java.util.List;

public class GamesViewModel extends ViewModel {
    private final IGamesRepository iGamesRepository;
    private MutableLiveData<List<GameApiResponse>> popularGames;
    private MutableLiveData<List<GameApiResponse>> bestGames;
    private MutableLiveData<List<GameApiResponse>> latestGames;
    private MutableLiveData<List<GameApiResponse>> incomingGames;
    private MutableLiveData<List<GameApiResponse>> exploreGames;
    private MutableLiveData<List<GameApiResponse>> franchiseGames;
    private MutableLiveData<List<GameApiResponse>> companyGames;
    private MutableLiveData<List<GameApiResponse>> genreGames;

    public GamesViewModel(IGamesRepository iGamesRepository) {
        this.iGamesRepository = iGamesRepository;
    }
    public MutableLiveData<GameApiResponse> getGame(int id) {
        return iGamesRepository.fetchGame(id);
    }
    public MutableLiveData<List<GameApiResponse>> getPopularGames(long lastUpdate) {
        if (popularGames == null){
            return popularGames = iGamesRepository.fetchPopularGames(lastUpdate);
        }
        return popularGames;
    }
    public MutableLiveData<List<GameApiResponse>> getBestGames(long lastUpdate) {
        if (bestGames == null){
            return bestGames = iGamesRepository.fetchBestGames(lastUpdate);
        }
        return bestGames;
    }
    public MutableLiveData<List<GameApiResponse>> getLatestGames(long lastUpdate) {
        if (latestGames == null){
            return latestGames = iGamesRepository.fetchLatestGames(lastUpdate);
        }
        return latestGames;
    }
    public MutableLiveData<List<GameApiResponse>> getIncomingGames(long lastUpdate) {
        if (incomingGames == null) {
            return incomingGames = iGamesRepository.fetchIncomingGames(lastUpdate);
        }
        return incomingGames;
    }
    public MutableLiveData<List<GameApiResponse>> getExploreGames(long lastUpdate) {
        if (exploreGames == null || exploreGames.getValue().isEmpty()){
            return exploreGames = iGamesRepository.fetchExploreGames(lastUpdate);
        }
        return exploreGames;
    }

    public MutableLiveData<List<GameApiResponse>> getCompanyGames(String company) {
        if (companyGames == null){
            return companyGames = iGamesRepository.fetchCompanyGames(company);
        }
        return companyGames;
    }
    public MutableLiveData<List<GameApiResponse>> getGenreGames(String genre) {
        if (genreGames == null){
            return genreGames = iGamesRepository.fetchGenreGames(genre);
        }
        return genreGames;
    }
    public MutableLiveData<List<GameApiResponse>> getFranchiseGames(String franchise) {
        if (franchiseGames == null){
            return franchiseGames = iGamesRepository.fetchFranchiseGames(franchise);
        }
        return franchiseGames;
    }

    public MutableLiveData<List<GameApiResponse>> getWantedGames(boolean isFirstLoading) {
        return iGamesRepository.getWantedGames(isFirstLoading);

    }

    public MutableLiveData<List<GameApiResponse>> getPlayingGames(boolean isFirstLoading) {
        return iGamesRepository.getPlayingGames(isFirstLoading);
    }

    public MutableLiveData<List<GameApiResponse>> getPlayedGames(boolean isFirstLoading) {
        return iGamesRepository.getPlayedGames(isFirstLoading);
    }
    public void updateWantedGame(GameApiResponse game){
        iGamesRepository.updateWantedGame(game);
    }
    public void updatePlayingGame(GameApiResponse game) {
        iGamesRepository.updatePlayingGame(game);
    }
    public void updatePlayedGame(GameApiResponse game) {
        iGamesRepository.updatePlayedGame(game);
    }
    public MutableLiveData<List<GameApiResponse>> getForYouGames(long lastUpdate) {
        return iGamesRepository.getForYouGames(lastUpdate);
    }

    public MutableLiveData<List<GameApiResponse>> getSearchedGames(String userInput) {
        return iGamesRepository.getSearchedGames(userInput);
    }

    public MutableLiveData<List<GameApiResponse>> getSearchedGames(String genre, String platform, String year) {
        return iGamesRepository.getSearchedGames(genre, platform, year);
    }

    public MutableLiveData<List<GameApiResponse>> getSimilarGames(List<Integer> similarGames) {
        return iGamesRepository.getSimilarGames(similarGames);
    }

    public MutableLiveData<List<GameApiResponse>> getAllPopularGames() {
        return iGamesRepository.getAllPopularGames();
    }
    public MutableLiveData<List<GameApiResponse>> getAllBestGames() {
        return iGamesRepository.getAllBestGames();
    }
    public MutableLiveData<List<GameApiResponse>> getAllLatestGames() {
        return iGamesRepository.getAllLatestGames();
    }
    public MutableLiveData<List<GameApiResponse>> getAllIncomingGames() {
        return iGamesRepository.getAllIncomingGames();
    }
}
