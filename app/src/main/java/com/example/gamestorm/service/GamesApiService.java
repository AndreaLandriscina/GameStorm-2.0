package com.example.gamestorm.service;

import static com.example.gamestorm.util.Constants.CLIENT_ID;
import static com.example.gamestorm.util.Constants.CONTENT_TYPE;
import static com.example.gamestorm.util.Constants.TOKEN_API;
import static com.example.gamestorm.util.Constants.TOP_HEADLINES_ENDPOINT;

import com.example.gamestorm.model.GameApiResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface GamesApiService {
    @POST(TOP_HEADLINES_ENDPOINT)
    Call<List<GameApiResponse>> getGames(
            @Header(CONTENT_TYPE) String contentType,
            @Header(CLIENT_ID) String clientID,
            @Header(TOKEN_API) String authorization,
            @Body RequestBody query);
}