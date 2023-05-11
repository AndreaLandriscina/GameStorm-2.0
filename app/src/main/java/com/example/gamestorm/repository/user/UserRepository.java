package com.example.gamestorm.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.model.User;
import com.example.gamestorm.source.games.BaseGamesLocalDataSource;
import com.example.gamestorm.source.games.GameCallback;
import com.example.gamestorm.source.user.BaseUserAuthenticationRemoteDataSource;
import com.example.gamestorm.source.user.BaseUserDataRemoteDataSource;

import java.util.List;

public class UserRepository implements IUserRepository, UserResponseCallback, GameCallback {
    private static final String TAG = UserRepository.class.getSimpleName();

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final BaseGamesLocalDataSource baseGamesLocalDataSource;
    private final MutableLiveData<User> userMutableLiveData;
    private final MutableLiveData<User> userWantedGamesMutableLiveData;
    private final MutableLiveData<User> userPlayingGamesMutableLiveData;
    private final MutableLiveData<User> userPlayedGamesMutableLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource,
                          BaseGamesLocalDataSource baseGamesLocalDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.baseGamesLocalDataSource = baseGamesLocalDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userWantedGamesMutableLiveData = new MutableLiveData<>();
        this.userPlayingGamesMutableLiveData = new MutableLiveData<>();
        this.userPlayedGamesMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
        this.baseGamesLocalDataSource.setGameCallback(this);
    }

    @Override
    public MutableLiveData<User> getUser(String name, String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            signUp(name, email, password);
        }
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<User> getGoogleUser(String idToken) {
        signInWithGoogle(idToken);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<User> getUserWantedGames(String idToken) {
        userDataRemoteDataSource.getUserWantedGames(idToken);
        return userWantedGamesMutableLiveData;
    }

    @Override
    public MutableLiveData<User> getUserPlayingGames(String idToken) {
        userDataRemoteDataSource.getUserPlayingGames(idToken);
        return userPlayingGamesMutableLiveData;
    }

    @Override
    public MutableLiveData<User> getUserPlayedGames(String idToken) {
        userDataRemoteDataSource.getUserPlayedGames(idToken);
        return userPlayedGamesMutableLiveData;
    }

    @Override
    public MutableLiveData<User> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }

    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public void signUp(String name, String email, String password) {
        userRemoteDataSource.signUp(name, email, password);
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }

    @Override
    public void signInWithGoogle(String token) {
        userRemoteDataSource.signInWithGoogle(token);
    }

    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        userMutableLiveData.postValue(null);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        userMutableLiveData.postValue(user);
    }

    @Override
    public void onSuccessFromRemoteDatabase(List<GameApiResponse> gameApiResponses, String i) {
        baseGamesLocalDataSource.insertGames(gameApiResponses, i);
    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        //Result.Error result = new Result.Error(message);
        //userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessLogout() {
        baseGamesLocalDataSource.deleteAll();
    }

    @Override
    public void onSuccessFromRemote(List<GameApiResponse> gameApiResponse, String i) {

    }

    @Override
    public void onSuccessFromLocal(List<GameApiResponse> gameApiResponses, String i) {
        //Result.GamesResponseSuccess result = new Result.GamesResponseSuccess(gameApiResponses);
        //userFavoriteNewsMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessDeletion() {
        //Result.UserResponseSuccess result = new Result.UserResponseSuccess(null);
        //userMutableLiveData.postValue(result);
    }

    @Override
    public void onGameWantedStatusChanged(GameApiResponse updatedGame, List<GameApiResponse> wantedGames) {

    }

    @Override
    public void onGameWantedStatusChanged(List<GameApiResponse> wantedGames) {

    }

    @Override
    public void onGamePlayingStatusChanged(GameApiResponse updatedGame, List<GameApiResponse> wantedGames) {

    }

    @Override
    public void onGamePlayingStatusChanged(List<GameApiResponse> playingGames) {

    }

    @Override
    public void onGamePlayedStatusChanged(GameApiResponse updatedGame, List<GameApiResponse> wantedGames) {

    }

    @Override
    public void onGamePlayedStatusChanged(List<GameApiResponse> playedGames) {

    }

    @Override
    public void onSuccessFromCloudReading(List<GameApiResponse> wantedGames, String wanted) {

    }

    @Override
    public void onSuccessFromCloudWriting(GameApiResponse game, String wanted) {

    }

    @Override
    public void onFailureFromCloud(Exception e) {

    }

    @Override
    public void onSuccessSynchronization() {

    }
}
