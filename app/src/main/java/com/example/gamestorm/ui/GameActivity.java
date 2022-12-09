package com.example.gamestorm.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.example.gamestorm.Model.Game;
import com.example.gamestorm.Model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.Repository.GamesRepository;
import com.example.gamestorm.Repository.IGamesRepository;
import com.example.gamestorm.databinding.ActivityGameBinding;
import com.example.gamestorm.util.ResponseCallback;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameActivity extends AppCompatActivity implements ResponseCallback {

    int idGame = 0;
    private IGamesRepository iGamesRepository;
    private ArrayList<Game> games;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar();
        //toolBarLayout.setTitle(getTitle());
        Intent intent = getIntent();
        idGame = intent.getIntExtra("idGame", 0);
        iGamesRepository = new GamesRepository(getApplication(), this);
        games = new ArrayList<Game>();

        iGamesRepository.fetchGames(10000);
    }



    private void setScreenshot() throws JSONException, IOException {
        String query = "fields screenshots.url; where id = 1020;";


    }

    private void setTitle() throws JSONException {
        TextView textView = findViewById(R.id.gameName);
        
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

    private void setReleaseDate() throws JSONException {
        TextView textView1 = findViewById(R.id.releaseDate);
        //String releaseDate = gameInfo.getReleaseDate();
        //textView1.setText(releaseDate);
    }

    private void setGenres() throws JSONException {
        //String[] genres = gameInfo.getGenre();
        //showGenres(genres);
    }

    private void setPlatforms() throws JSONException {
        /*
        String[] platforms = gameInfo.getPlatforms();
        TextView platformText = findViewById(R.id.platformText);
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < platforms.length; i++){
            text.append(platforms[i]);
            if (i != platforms.length - 1){
                text.append(" - ");
            }
        }
        platformText.setText(text);

         */
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showGenres(String[] genres) throws JSONException {
        LinearLayoutCompat linearLayoutCompat = findViewById(R.id.genresLayout);

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


    @Override
    public void onSuccess(List<GameApiResponse> gamesList, long lastUpdate) {
        //this.games.addAll(gamesList);
    }


    @Override
    public void onFailure(String errorMessage) {
        Log.i("onFailure", errorMessage);
    }

    @Override
    public void onGameFavoriteStatusChanged(Game game) {

    }
}


