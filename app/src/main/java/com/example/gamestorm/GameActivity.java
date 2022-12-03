package com.example.gamestorm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;


import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gamestorm.databinding.ActivityGameBinding;

import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {
    String response = null;
    GameInfo gameInfo = null;
    int idGame = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar();
        //toolBarLayout.setTitle(getTitle());
        Intent intent = getIntent();
        idGame = intent.getIntExtra("idGame", 0);


        try {
            response = callAPI(idGame);
            ImageView gameImageView = findViewById(R.id.gameImage);
            gameInfo = new GameInfo(gameImageView, response);
            gameInfo.setGameImage();
            setTitle();
            setGenres();
            setReleaseDate();
            setPlatforms();
            setGameSagaButton();
            setGameVersionButton();
            //franchise
            //company
            //logo
            //screenshot
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    private void setTitle() throws JSONException {
        TextView textView = findViewById(R.id.gameName);
        textView.setText(gameInfo.getName());
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
        Intent myIntent = new Intent(getApplicationContext(), GameSagaActivity.class);
        myIntent.putExtra("idGame", idGame);
        startActivity(myIntent);
    }
    private void showGameVesions(){
        Intent myIntent = new Intent(getApplicationContext(), GameVersionActivity.class);
        myIntent.putExtra("idGame", idGame);
        startActivity(myIntent);
    }

    private void setReleaseDate() throws JSONException {
        TextView textView1 = findViewById(R.id.releaseDate);
        String releaseDate = gameInfo.getReleaseDate();
        textView1.setText(releaseDate);
    }

    private void setGenres() throws JSONException {
        String[] genres = gameInfo.getGenre();
        showGenres(genres);
    }

    private void setPlatforms() throws JSONException {
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
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(getApplicationContext(), CategoryActivity.class);
                    int category = 0;
                    myIntent.putExtra("category", category);
                    startActivity(myIntent);
                }
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
    private String callAPI(int idGame) throws JSONException, IOException {
        String url = getString(R.string.urlAPI);
        API api = new API(getApplicationContext(), url);
        String query = "fields name, release_dates.date, genres.name, rating, cover.url, platforms.name; where id = 1020;";
        api.callAPI(query);
        return api.getResponse();
    }

}


