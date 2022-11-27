package com.example.gamestorm;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.gamestorm.databinding.ActivityGameBinding;

import org.json.JSONException;

import java.io.IOException;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.gamestorm.databinding.ActivityGameBinding binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        //toolBarLayout.setTitle(getTitle());
        Intent intent = getIntent();
        int idGame = intent.getIntExtra("idGame", 0);
        String response = null;

        try {
            response = callAPI(idGame);
            setGameImage(response);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    private String callAPI(int idGame) throws JSONException, IOException {
        TextView textView = findViewById(R.id.testo);
        API api = new API(getApplicationContext());
        String query = "fields screenshots.*; where id = 1950;";
        api.callAPI(query);
        String response = api.getResponse();
        textView.setText(response);
        return response;
    }

    private void setGameImage(String response) throws JSONException, IOException {
        ImageView gameImageView = findViewById(R.id.gameImage);
        GameImage image = new GameImage(gameImageView);
        image.setGameImage(response);
    }

}


