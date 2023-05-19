package com.example.gamestorm.repository.games;

import static com.example.gamestorm.util.Constants.FRESH_TIMEOUT;
import static com.example.gamestorm.util.Constants.FRESH_TIMEOUT_EXPLORE;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.source.games.BaseGamesDataSource;
import com.example.gamestorm.source.games.BaseGamesLocalDataSource;
import com.example.gamestorm.source.games.BaseSavedGamesDataSource;
import com.example.gamestorm.source.games.GameCallback;

import java.util.ArrayList;
import java.util.List;

public class GamesRepository implements IGamesRepository, GameCallback {
    private final BaseGamesDataSource gamesDataSource;
    private final BaseGamesLocalDataSource gamesLocalDataSource;
    private final BaseSavedGamesDataSource backupDataSource;
    private final MutableLiveData<List<GameApiResponse>> allGames = new MutableLiveData<>();
    private final MutableLiveData<List<GameApiResponse>> popularGames = new MutableLiveData<>();
    private final MutableLiveData<List<GameApiResponse>> bestGames = new MutableLiveData<>();
    private final MutableLiveData<List<GameApiResponse>> incomingGames = new MutableLiveData<>();
    private final MutableLiveData<List<GameApiResponse>> latestGames = new MutableLiveData<>();
    private final MutableLiveData<GameApiResponse> game = new MutableLiveData<>();
    private final MutableLiveData<List<GameApiResponse>> exploreGames;
    private final MutableLiveData<List<GameApiResponse>> companyGames = new MutableLiveData<>();
    private final MutableLiveData<List<GameApiResponse>> franchiseGames = new MutableLiveData<>();
    private final MutableLiveData<List<GameApiResponse>> wantedGamesMutableLiveData;
    private final MutableLiveData<List<GameApiResponse>> playingGamesMutableLiveData;
    private final MutableLiveData<List<GameApiResponse>> playedGamesMutableLiveData;
    private final MutableLiveData<List<GameApiResponse>> forYouGamesMutableLiveData;
    private final MutableLiveData<List<GameApiResponse>> genreGamesMutableLiveData;
    private final MutableLiveData<List<GameApiResponse>> searchedGamesMutableLiveData;
    private  MutableLiveData<List<GameApiResponse>> filteredGamesMutableLiveData;
    private final MutableLiveData<List<GameApiResponse>> similarGamesMutableLiveData;
    private final MutableLiveData<List<GameApiResponse>> allPopularGames;
    private final MutableLiveData<List<GameApiResponse>> allBestGames;
    private final MutableLiveData<List<GameApiResponse>> allLatestGames;
    private final MutableLiveData<List<GameApiResponse>> allIncomingGames;

    public GamesRepository(BaseGamesDataSource gamesDataSource, BaseGamesLocalDataSource gamesLocalDataSource, BaseSavedGamesDataSource backupDataSource) {
        this.gamesDataSource = gamesDataSource;
        this.gamesLocalDataSource = gamesLocalDataSource;
        this.backupDataSource = backupDataSource;
        this.wantedGamesMutableLiveData = new MutableLiveData<>();
        this.playingGamesMutableLiveData = new MutableLiveData<>();
        this.playedGamesMutableLiveData = new MutableLiveData<>();
        this.forYouGamesMutableLiveData = new MutableLiveData<>();
        this.genreGamesMutableLiveData = new MutableLiveData<>();
        this.searchedGamesMutableLiveData = new MutableLiveData<>();
        this.filteredGamesMutableLiveData = new MutableLiveData<>();
        this.similarGamesMutableLiveData = new MutableLiveData<>();
        this.exploreGames = new MutableLiveData<>();
        this.allPopularGames = new MutableLiveData<>();
        this.allBestGames = new MutableLiveData<>();
        this.allLatestGames = new MutableLiveData<>();
        this.allIncomingGames = new MutableLiveData<>();
        this.gamesDataSource.setGameCallback(this);
        this.gamesLocalDataSource.setGameCallback(this);
        this.backupDataSource.setGameCallback(this);
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> fetchPopularGames(long lastUpdate, boolean networkAvailable) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdate > FRESH_TIMEOUT && networkAvailable) {
            gamesDataSource.getPopularGames();
        } else {
            gamesLocalDataSource.getPopularGames();
        }
        return popularGames;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> fetchBestGames(long lastUpdate, boolean networkAvailable) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdate > FRESH_TIMEOUT && networkAvailable) {
            gamesDataSource.getBestGames();
        } else {
            gamesLocalDataSource.getBestGames();
        }
        return bestGames;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> fetchLatestGames(long lastUpdate, boolean networkAvailable) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdate > FRESH_TIMEOUT && networkAvailable) {
            gamesDataSource.getLatestGames();
        } else {
            gamesLocalDataSource.getLatestGames();
        }
        return latestGames;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> fetchIncomingGames(long lastUpdate, boolean networkAvailable) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdate > FRESH_TIMEOUT && networkAvailable) {
            gamesDataSource.getIncomingGames();
        } else {
            gamesLocalDataSource.getIncomingGames();
        }
        return incomingGames;
    }

    @Override
    public MutableLiveData<GameApiResponse> fetchGame(int id) {
        gamesLocalDataSource.getGame(id);
        return game;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> fetchExploreGames(boolean networkAvailable) {
        if (networkAvailable) {
            gamesDataSource.getExploreGames();
        } else {
            gamesLocalDataSource.getExploreGames();
        }
        return exploreGames;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> fetchCompanyGames(String company) {
        gamesDataSource.getCompanyGames(company);
        return companyGames;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> fetchFranchiseGames(String franchise) {
        gamesDataSource.getFranchiseGames(franchise);
        return franchiseGames;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> fetchGenreGames(String genre) {
        gamesDataSource.getGenreGames(genre);
        return genreGamesMutableLiveData;
    }

    @Override
    public void updateWantedGame(GameApiResponse game) {
        gamesLocalDataSource.updateWantedGame(game);
        if (game.isWanted()) {
            backupDataSource.addWantedGame(game);
        } else {
            backupDataSource.deleteWantedGame(game);
        }
    }

    @Override
    public void updatePlayingGame(GameApiResponse game) {
        gamesLocalDataSource.updatePlayingGame(game);
        if (game.isPlaying()) {
            backupDataSource.addPlayingGame(game);
        } else {
            backupDataSource.deletePlayingGame(game);
        }
    }

    @Override
    public void updatePlayedGame(GameApiResponse game) {
        gamesLocalDataSource.updatePlayedGame(game);
        if (game.isPlayed()) {
            backupDataSource.addPlayedGame(game);
        } else {
            backupDataSource.deletePlayedGame(game);
        }
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> getWantedGames(boolean isFirstLoading) {
        if (isFirstLoading) {
            backupDataSource.getWantedGames();
        } else {
            gamesLocalDataSource.getWantedGames();
        }
        return wantedGamesMutableLiveData;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> getPlayingGames(boolean isFirstLoading) {
        if (isFirstLoading) {
            backupDataSource.getPlayingGames();
        } else {
            gamesLocalDataSource.getPlayingGames();
        }
        return playingGamesMutableLiveData;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> getPlayedGames(boolean isFirstLoading) {
        if (isFirstLoading) {
            backupDataSource.getPlayedGames();
        } else {
            gamesLocalDataSource.getPlayedGames();
        }
        return playedGamesMutableLiveData;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> getSearchedGames(String userInput) {
        if (userInput != null)
            gamesDataSource.getSearchedGames(userInput);
        return searchedGamesMutableLiveData;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> getSimilarGames(List<Integer> similarGames) {
        gamesDataSource.getSimilarGames(similarGames);
        return similarGamesMutableLiveData;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> getAllPopularGames() {
        gamesDataSource.getAllPopularGames();
        return allPopularGames;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> getAllBestGames() {
        gamesDataSource.getAllBestGames();
        return allBestGames;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> getAllLatestGames() {
        gamesDataSource.getAllLatestGames();
        return allLatestGames;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> getAllIncomingGames() {
        gamesDataSource.getAllIncomingGames();
        return allIncomingGames;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> getFilteredGames(String genre, String platform, String year) {
        if (genre != null || platform != null){
            gamesDataSource.getFilteredGames(genre, platform, year);
        }

        //if (filteredGamesMutableLiveData.getValue() != null && !filteredGamesMutableLiveData.getValue().isEmpty())
        //    filteredGamesMutableLiveData = new MutableLiveData<>();
        return filteredGamesMutableLiveData;
    }

    @Override
    public MutableLiveData<List<GameApiResponse>> getForYouGames(long lastUpdate) {
        List<Integer> gamesId = new ArrayList<>();
        int limit = 0;
        if (playedGamesMutableLiveData != null && playedGamesMutableLiveData.getValue() != null) {
            int size = playedGamesMutableLiveData.getValue().size();
            if (size <= 2) {
                limit = 2;
            } else if (size <=6) {
                limit = 5;
            } else {
                limit = 10;
            }
            int cont = 0;
            for (int i = 0; i < size && cont <= limit; i++) {
                GameApiResponse game = playedGamesMutableLiveData.getValue().get(i);
                gamesId.add(game.getSimilarGames().get(0));
                gamesId.add(game.getSimilarGames().get(4));
                cont+=2;
            }
        }
        assert playedGamesMutableLiveData != null;
        gamesDataSource.getForYouGames(gamesId, limit);
        return forYouGamesMutableLiveData;
    }


    @Override
    public void onSuccessFromRemote(List<GameApiResponse> gameApiResponse, String i) {
        gamesLocalDataSource.insertGames(gameApiResponse, i);
    }

    @Override
    public void onSuccessFromLocal(List<GameApiResponse> gameApiResponses, String i) {
        List<GameApiResponse> gamesList = allGames.getValue();
        if (gamesList != null) {
            gamesList.addAll(gameApiResponses);
        }

        allGames.postValue(gamesList);
        switch (i) {
            case "POPULAR":
                popularGames.postValue(gameApiResponses);
                break;
            case "BEST":
                bestGames.postValue(gameApiResponses);
                break;
            case "INCOMING":
                incomingGames.postValue(gameApiResponses);
                break;
            case "LATEST":
                latestGames.postValue(gameApiResponses);
                break;
            case "SINGLE":
                game.postValue(gameApiResponses.get(0));
                break;
            case "EXPLORE":
                exploreGames.postValue(gameApiResponses);
                break;
            case "SEARCHED":
                searchedGamesMutableLiveData.postValue(gameApiResponses);
                break;
            case "FILTERED" :
                Log.i("FILTERED","FILTERED");
                filteredGamesMutableLiveData.postValue(gameApiResponses);
                break;
            case "FRANCHISE":
                franchiseGames.postValue(gameApiResponses);
                break;
            case "COMPANY":
                companyGames.postValue(gameApiResponses);
                break;
            case "GENRE":
                genreGamesMutableLiveData.postValue(gameApiResponses);
                break;
            case "SIMILAR":
                similarGamesMutableLiveData.postValue(gameApiResponses);
                break;
            case "WANTED":
                wantedGamesMutableLiveData.postValue(gameApiResponses);
                break;
            case "PLAYING":
                playingGamesMutableLiveData.postValue(gameApiResponses);
                break;
            case "PLAYED":
                playedGamesMutableLiveData.postValue(gameApiResponses);
                break;
            case "FORYOU":
                forYouGamesMutableLiveData.postValue(gameApiResponses);
                break;
            case "ALLPOPULAR":
                allPopularGames.postValue(gameApiResponses);
                break;
            case "ALLBEST":
                allBestGames.postValue(gameApiResponses);
                break;
            case "ALLLATEST":
                allLatestGames.postValue(gameApiResponses);
                break;
            case "ALLINCOMING":
                allIncomingGames.postValue(gameApiResponses);
                break;
        }
    }

    @Override
    public void onSuccessDeletion() {
        Log.i("tutto", "eliminato");
    }

    @Override
    public void onGameWantedStatusChanged(GameApiResponse updatedGame, List<GameApiResponse> wantedGames) {
        List<GameApiResponse> allWanted = wantedGamesMutableLiveData.getValue();

        if (allWanted != null) {
            for (GameApiResponse gameApiResponse : allWanted) {
                if (gameApiResponse.getId() == updatedGame.getId()) {
                    Log.i("ongamewantedstatuschanged", updatedGame.toString());
                    allWanted.set(allWanted.indexOf(gameApiResponse), updatedGame);
                }
            }
        }
        wantedGamesMutableLiveData.postValue(allWanted);
    }

    @Override
    public void onGameWantedStatusChanged(List<GameApiResponse> wantedGames) {
        List<GameApiResponse> notSynchronizedNewsList = new ArrayList<>();
        for (GameApiResponse game : wantedGames) {
            if (!game.isSynchronized()) {
                notSynchronizedNewsList.add(game);
            }
        }

        if (!notSynchronizedNewsList.isEmpty()) {
            backupDataSource.synchronizeWantedGame(notSynchronizedNewsList);
        }

        wantedGamesMutableLiveData.postValue(wantedGames);
    }

    @Override
    public void onGamePlayingStatusChanged(GameApiResponse updatedGame, List<GameApiResponse> playingGames) {
        List<GameApiResponse> allPlaying = playingGamesMutableLiveData.getValue();
        if (allPlaying != null) {
            for (GameApiResponse gameApiResponse : allPlaying) {
                if (gameApiResponse.getId() == updatedGame.getId()) {
                    allPlaying.set(allPlaying.indexOf(gameApiResponse), updatedGame);
                }
            }
        }
        playingGamesMutableLiveData.postValue(allPlaying);
    }

    @Override
    public void onGamePlayingStatusChanged(List<GameApiResponse> playingGames) {
        List<GameApiResponse> notSynchronizedNewsList = new ArrayList<>();
        for (GameApiResponse game : playingGames) {
            if (!game.isSynchronized()) {
                notSynchronizedNewsList.add(game);
            }
        }

        if (!notSynchronizedNewsList.isEmpty()) {
            backupDataSource.synchronizePlayingGame(notSynchronizedNewsList);
        }

        playingGamesMutableLiveData.postValue(playingGames);
    }

    @Override
    public void onGamePlayedStatusChanged(GameApiResponse updatedGame, List<GameApiResponse> playedGames) {
        List<GameApiResponse> allPlayed = playedGamesMutableLiveData.getValue();
        if (allPlayed != null) {
            for (GameApiResponse gameApiResponse : allPlayed) {
                if (gameApiResponse.getId() == updatedGame.getId()) {
                    allPlayed.set(allPlayed.indexOf(gameApiResponse), updatedGame);
                }
            }
        }
        playedGamesMutableLiveData.postValue(allPlayed);
    }

    @Override
    public void onGamePlayedStatusChanged(List<GameApiResponse> playedGames) {
        List<GameApiResponse> notSynchronizedNewsList = new ArrayList<>();
        for (GameApiResponse game : playedGames) {
            if (!game.isSynchronized()) {
                notSynchronizedNewsList.add(game);
            }
        }
        if (!notSynchronizedNewsList.isEmpty()) {
            backupDataSource.synchronizePlayedGame(notSynchronizedNewsList);
        }
        playedGamesMutableLiveData.postValue(playedGames);
    }

    @Override
    public void onSuccessFromCloudReading(List<GameApiResponse> games, String wanted) {
        if (games != null) {
            for (GameApiResponse gameApiResponse : games) {
                gameApiResponse.setSynchronized(true);
            }
        }
        switch (wanted) {
            case "WANTED":
                gamesLocalDataSource.insertGames(games, "WANTED");
                wantedGamesMutableLiveData.postValue(games);
                break;
            case "PLAYING":
                gamesLocalDataSource.insertGames(games, "PLAYING");
                playingGamesMutableLiveData.postValue(games);
                break;
            case "PLAYED":
                gamesLocalDataSource.insertGames(games, "PLAYED");
                playedGamesMutableLiveData.postValue(games);
                break;
        }
    }

    @Override
    public void onSuccessFromCloudWriting(GameApiResponse game, String wanted) {
        switch (wanted) {
            case "WANTED":
                if (game != null && !game.isWanted()) {
                    game.setSynchronized(false);
                }
                gamesLocalDataSource.updateWantedGame(game);
                backupDataSource.getWantedGames();
                break;
            case "PLAYING":
                if (game != null && !game.isPlaying()) {
                    game.setSynchronized(false);
                }
                gamesLocalDataSource.updatePlayingGame(game);
                backupDataSource.getPlayingGames();
                break;
            case "PLAYED":
                if (game != null && !game.isPlayed()) {
                    game.setSynchronized(false);
                }
                gamesLocalDataSource.updatePlayedGame(game);
                backupDataSource.getPlayedGames();
                break;
        }
    }

    @Override
    public void onFailureFromCloud(Exception e) {
        Log.e(getClass().getSimpleName(), "onFailureFromCloud");
    }

    @Override
    public void onSuccessSynchronization() {
        Log.d("GamesRepository", "Games synchronized from remote");
    }
}
