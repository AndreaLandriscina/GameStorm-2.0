package com.example.gamestorm.repository;

import static com.example.gamestorm.util.Constants.CLIENT_ID_VALUE;
import static com.example.gamestorm.util.Constants.CONTENT_TYPE_VALUE;
import static com.example.gamestorm.util.Constants.TOKEN_API_VALUE;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.service.GamesApiService;
import com.example.gamestorm.util.ResponseCallback;
import com.example.gamestorm.util.ServiceLocator;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GamesRepository implements IGamesRepository{
    private final Application application;
    private final GamesApiService gamesApiService;
    private final ResponseCallback responseCallback;

    public GamesRepository(Application application, ResponseCallback responseCallback) {
        this.application = application;
        this.gamesApiService = ServiceLocator.getInstance().getGamesApiService();
        this.responseCallback = responseCallback;
    }

    @Override
    public void fetchGames(String query, long lastUpdate,int count) {
        RequestBody body = RequestBody.create(MediaType.parse("text-plain"), query);
        Call<List<GameApiResponse>> gameApiResponseCall = gamesApiService.getGames(
                CONTENT_TYPE_VALUE,
                CLIENT_ID_VALUE,
                TOKEN_API_VALUE,
                body);

        gameApiResponseCall.enqueue(new Callback<List<GameApiResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<GameApiResponse>> call, @NonNull Response<List<GameApiResponse>> response) {
                if (response.body() != null && response.isSuccessful()) {
                    List<GameApiResponse> gameApiResponses = response.body();

                    Log.i("response", gameApiResponses.toString());

                    responseCallback.onSuccess(gameApiResponses, 1000,count);
                } else {
                    responseCallback.onFailure(application.getString(R.string.error_retrieving_games));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GameApiResponse>> call, @NonNull Throwable t) {
                Toast.makeText(application, application.getString(R.string.error_retrieving_games), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
