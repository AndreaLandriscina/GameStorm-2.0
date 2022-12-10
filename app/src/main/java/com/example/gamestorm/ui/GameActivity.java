package com.example.gamestorm.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.example.gamestorm.Model.Cover;
import com.example.gamestorm.Model.Game;
import com.example.gamestorm.Model.GameApiResponse;
import com.example.gamestorm.Model.Genre;
import com.example.gamestorm.Model.ReleaseDate;
import com.example.gamestorm.R;
import com.example.gamestorm.Repository.GamesRepository;
import com.example.gamestorm.Repository.IGamesRepository;
import com.example.gamestorm.databinding.ActivityGameBinding;
import com.example.gamestorm.util.ResponseCallback;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GameActivity extends AppCompatActivity implements ResponseCallback {

    int idGame = 0;
    private IGamesRepository iGamesRepository;
    private List<GameApiResponse> games;
    private GameApiResponse game;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar();
        //toolBarLayout.setTitle(getTitle());
        Intent intent = getIntent();
        idGame = intent.getIntExtra("idGame", 0);
        progressBar = findViewById(R.id.progressBar);
        iGamesRepository = new GamesRepository(getApplication(), this);
        progressBar.setVisibility(View.VISIBLE);
        String query = "fields id,name, cover.url; search \"God of war\";";
        iGamesRepository.fetchGames(query,10000);

        games = new ArrayList<>();
    }

    @Override
    public void onSuccess(List<GameApiResponse> gamesList, long lastUpdate) {
        progressBar.setVisibility(View.GONE);
        this.games.addAll(gamesList);
        game = games.get(0);
        TextView gameNameView = findViewById(R.id.gameName);
        gameNameView.setText(game.getName());

        ImageView gameImage = findViewById(R.id.gameImage);
        String uriString = game.getCover().getUrl();
        String newUri = uriString.replace("thumb", "cover_big_2x");
        Picasso.get().load(newUri).into(gameImage);

        TextView releaseDateView = findViewById(R.id.releaseDate);
        String date = game.getReleaseDate();
        releaseDateView.setText(date);

        LinearLayoutCompat linearLayoutCompat = findViewById(R.id.genresLayout);

        List<String> genres = game.getGenresString();
        for (String genre : genres){
            TextView textView = new TextView(this);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(20);
            textView.setPadding(15,10,15,10);
            textView.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.rounded_corner));
            textView.setText(genre);
            textView.setOnClickListener(v -> {
                /*
                Intent myIntent = new Intent(getApplicationContext(), CategoryActivity.class);
                int category = 0;
                myIntent.putExtra("category", category);
                startActivity(myIntent);

                 */
            });
            linearLayoutCompat.addView(textView);
        }

        List<String> platforms = game.getPlatformsString();
        TextView platformText = findViewById(R.id.platformText);
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < platforms.size(); i++){
            text.append(platforms.get(i));
            if (i != platforms.size() - 1){
                text.append(" - ");
            }
        }
        platformText.setText(text);
    }


    @Override
    public void onFailure(String errorMessage) {
        Log.i("onFailure", errorMessage);
    }

    @Override
    public void onGameFavoriteStatusChanged(GameApiResponse game) {

    }


    private void setGameVersionButton() {
        Button showGameVersionsButton = findViewById(R.id.showVersionsButton);
        showGameVersionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGameVesions();
            }
        });
    }

    private void setGameSagaButton() {
        Button showGameSagaButton = findViewById(R.id.showGameSagaButton);
        showGameSagaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGameSaga();
            }
        });
    }

    private void showGameSaga(){
        /*
        Intent myIntent = new Intent(getApplicationContext(), GameSagaActivity.class);
        myIntent.putExtra("idGame", idGame);
        startActivity(myIntent);
         */
    }

    private void showGameVesions(){
        /*
        Intent myIntent = new Intent(getApplicationContext(), GameVersionActivity.class);
        myIntent.putExtra("idGame", idGame);
        startActivity(myIntent);
        */

    }

    private void setToolbar() {
        com.example.gamestorm.databinding.ActivityGameBinding binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }
//1020



}


