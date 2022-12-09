package com.example.gamestorm.Repository;

import static com.example.gamestorm.util.Constants.CLIENT_ID;
import static com.example.gamestorm.util.Constants.CLIENT_ID_VALUE;
import static com.example.gamestorm.util.Constants.CONTENT_TYPE;
import static com.example.gamestorm.util.Constants.CONTENT_TYPE_VALUE;
import static com.example.gamestorm.util.Constants.TOKEN_API;
import static com.example.gamestorm.util.Constants.TOKEN_API_VALUE;

import android.app.Application;
import android.util.Log;

import com.example.gamestorm.Model.Game;
import com.example.gamestorm.Model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.service.GamesApiService;
import com.example.gamestorm.util.Constants;
import com.example.gamestorm.util.ResponseCallback;
import com.example.gamestorm.util.ServiceLocator;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GamesRepository implements IGamesRepository{
    private final Application application;
    private final GamesApiService gamesApiService;
    //private final NewsDao newsDao;
    private final ResponseCallback responseCallback;

    public GamesRepository(Application application, ResponseCallback responseCallback) {
        this.application = application;
        this.gamesApiService = ServiceLocator.getInstance().getGamesApiService();
        //this.newsDao = newsDao;
        this.responseCallback = responseCallback;
    }

    @Override
    public void fetchGames(long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the news from the Web Service if the last download
        // of the news has been performed more than FRESH_TIMEOUT value ago
        if (true) {
        String query = "fields name, cover;";
        RequestBody body = RequestBody.create(MediaType.parse("text-plain"), query);
        Call<List<GameApiResponse>> gameApiResponseCall = gamesApiService.getGames(
                CONTENT_TYPE_VALUE,
                CLIENT_ID_VALUE,
                TOKEN_API_VALUE,
                body);

        gameApiResponseCall.enqueue(new Callback<List<GameApiResponse>>() {
            @Override
            public void onResponse(Call<List<GameApiResponse>> call, Response<List<GameApiResponse>> response) {
                if (response.body() != null && response.isSuccessful()) {
                    List<GameApiResponse> gamesList = response.body();
                    Log.i("response",gamesList.toString());
                    GameApiResponse gameApiResponse = response.body().get(0);
                    String name = gameApiResponse.getName();
                    Log.i("response", name);

                    responseCallback.onSuccess(gamesList, 1000);
                    //saveDataInDatabase(newsList);
                } else {
                    Log.i("onfailure", "no");
                    responseCallback.onFailure(application.getString(R.string.error_retrieving_games));
                }
            }

            @Override
            public void onFailure(Call<List<GameApiResponse>> call, Throwable t) {

            }

        });
        } else {
                //Log.d(TAG, application.getString(R.string.data_read_from_local_database));
                //readDataFromDatabase(lastUpdate);
        }
    }

    @Override
    public void updateGames(Game game) {

    }

    @Override
    public void getFavoriteGames() {

    }

    @Override
    public void deleteFavoriteGames() {

    }
}
