package com.example.gamestorm.util;

import static com.example.gamestorm.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.gamestorm.util.Constants.ID_TOKEN;

import android.app.Application;

import com.example.gamestorm.database.GamesRoomDatabase;
import com.example.gamestorm.repository.games.GamesRepository;
import com.example.gamestorm.repository.games.IGamesRepository;
import com.example.gamestorm.repository.user.IUserRepository;
import com.example.gamestorm.repository.user.UserRepository;
import com.example.gamestorm.service.GamesApiService;
import com.example.gamestorm.source.games.BaseGamesDataSource;
import com.example.gamestorm.source.games.BaseGamesLocalDataSource;
import com.example.gamestorm.source.games.BaseSavedGamesDataSource;
import com.example.gamestorm.source.games.GamesDataSource;
import com.example.gamestorm.source.games.GamesLocalDataSource;
import com.example.gamestorm.source.games.SavedGamesDataSource;
import com.example.gamestorm.source.user.BaseUserAuthenticationRemoteDataSource;
import com.example.gamestorm.source.user.BaseUserDataRemoteDataSource;
import com.example.gamestorm.source.user.UserAuthenticationRemoteDataSource;
import com.example.gamestorm.source.user.UserDataRemoteDataSource;

import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {}

    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    public GamesApiService getGamesApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.GAME_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        return retrofit.create(GamesApiService.class);
    }
    public GamesRoomDatabase getGamesDao(Application application) {
        return GamesRoomDatabase.getDatabase(application);
    }
    public IGamesRepository getGamesRepository(Application application) throws GeneralSecurityException, IOException {
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);
        BaseGamesDataSource gamesDataSource;
        BaseGamesLocalDataSource gamesLocalDataSource;
        BaseSavedGamesDataSource baseSavedGamesDataSource;
        DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(application);
        gamesDataSource = new GamesDataSource();
        gamesLocalDataSource = new GamesLocalDataSource(getGamesDao(application), sharedPreferencesUtil, dataEncryptionUtil);
        baseSavedGamesDataSource = new SavedGamesDataSource(dataEncryptionUtil.
                readSecretDataWithEncryptedSharedPreferences(
                        ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN));
        return new GamesRepository(gamesDataSource, gamesLocalDataSource, baseSavedGamesDataSource);
    }
    public IUserRepository getUserRepository(Application application) {
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);

        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource =
                new UserAuthenticationRemoteDataSource();

        BaseUserDataRemoteDataSource userDataRemoteDataSource =
                new UserDataRemoteDataSource(sharedPreferencesUtil);

        DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(application);

        BaseGamesLocalDataSource newsLocalDataSource =
                new GamesLocalDataSource(getGamesDao(application), sharedPreferencesUtil,
                        dataEncryptionUtil);

        return new UserRepository(userRemoteAuthenticationDataSource,
                userDataRemoteDataSource, newsLocalDataSource);
    }
}
