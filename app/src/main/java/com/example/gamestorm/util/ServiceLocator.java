package com.example.gamestorm.util;

import com.example.gamestorm.service.GamesApiService;

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
}
