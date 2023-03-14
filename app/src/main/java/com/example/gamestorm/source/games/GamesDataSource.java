package com.example.gamestorm.source.games;

import static com.example.gamestorm.util.Constants.CLIENT_ID_VALUE;
import static com.example.gamestorm.util.Constants.CONTENT_TYPE_VALUE;
import static com.example.gamestorm.util.Constants.TOKEN_API_VALUE;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.service.GamesApiService;
import com.example.gamestorm.util.ServiceLocator;

import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GamesDataSource extends BaseGamesDataSource {
    private final GamesApiService gamesApiService;
    private final String fields;

    public GamesDataSource() {
        this.gamesApiService = ServiceLocator.getInstance().getGamesApiService();
        fields = "fields name, videos.*, franchises.name, similar_games, first_release_date, genres.name, total_rating," +
                " total_rating_count, cover.url, involved_companies.company.name, platforms.name, summary, screenshots.url, follows; ";
    }

    public void getGames(String query, String i) {
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
                        gameCallback.onSuccessFromRemote(gameApiResponses, i);
                    } else {
                        Log.e(getClass().getName(), "error");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<GameApiResponse>> call, @NonNull Throwable t) {
                    Log.e(getClass().getSimpleName(), "error");
                }
        });
    }

    @Override
    public void getGames(String query) {
        getGames(query, "SINGLE");
    }

    @Override
    public void getGame(int id) {
        String query = fields + "where id = " + id + ";";
        getGames(query);
    }

    @Override
    public void getPopularGames() {
        String query = fields + "where follows!=null; sort follows desc; limit 20;";
        getGames(query, "POPULAR");
    }
    @Override
    public void getBestGames() {
        String query = fields + "where total_rating_count>1000;sort total_rating desc; limit 20;";
        getGames(query,"BEST");
    }
    @Override
    public void getLatestGames() {
        String query = fields + "where first_release_date <= " + currentDate() + ";sort first_release_date desc; limit 20;";
        getGames(query,"LATEST");
    }
    @Override
    public void getIncomingGames() {
        String query = fields + "where first_release_date > " + currentDate() + ";sort first_release_date asc; limit 20;";
        getGames(query, "INCOMING");
    }

    @Override
    public void getExploreGames() {
        String query = fields + "where first_release_date <= " + currentDate() + " & cover.url != null;  limit 300;";
        getGames(query, "EXPLORE");
    }

    @Override
    public void getCompanyGames(String company) {
        String query = fields + "where involved_companies.company.name = \"" + company + "\"; limit 30;";
        getGames(query, "COMPANY");
    }

    @Override
    public void getFranchiseGames(String franchise) {
        String query = fields + " where franchises.name = \"" + franchise + "\"; limit 30;";
        getGames(query, "FRANCHISE");
    }

    @Override
    public void getGenreGames(String genre) {
        String query = fields + "where genres.name = \"" + genre + "\" & total_rating > 85 & first_release_date > 1262304000; limit 30;";
        getGames(query, "GENRE");
    }

    @Override
    public void getSearchedGames(String userInput) {
        String query = fields + "where first_release_date < " + System.currentTimeMillis() / 1000 + " & version_parent = null;search \"" + userInput + "\"; limit 60;";
        getGames(query, "SEARCHED");
    }

    @Override
    public void getSimilarGames(List<Integer> similarGames) {
        StringBuilder ids = new StringBuilder();
        if (similarGames != null){
            for (Integer id : similarGames){
                ids.append(id);
                if (similarGames.lastIndexOf(id) != similarGames.size() - 1){
                    ids.append(",");
                }
            }
        }
        String query = fields + "where id = (" + ids + "); limit 15;";
        getGames(query, "SIMILAR");
    }

    @Override
    public void getForYouGames(String genre, int size) {
        int limit;
        if (size <= 3) {
            limit = 2;
        } else if (size <=6) {
            limit = 4;
        } else {
            limit = 8;
        }
        String query = fields + "where genres.name= \"" + genre + "\"; limit " + limit + ";";
        getGames(query, "FORYOU");
    }

    private long currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis() / 1000;
    }
}