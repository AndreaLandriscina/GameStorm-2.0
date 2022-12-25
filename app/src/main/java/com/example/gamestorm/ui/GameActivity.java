package com.example.gamestorm.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.example.gamestorm.adapter.RecyclerData;
import com.example.gamestorm.adapter.RecyclerScreenshotsViewAdapter;
import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.GamesRepository;
import com.example.gamestorm.repository.IGamesRepository;
import com.example.gamestorm.databinding.ActivityGameBinding;
import com.example.gamestorm.util.ResponseCallback;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import J.N;

public class GameActivity extends AppCompatActivity implements ResponseCallback {

    int idGame = 0;
    private List<GameApiResponse> games;
    private GameApiResponse game;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ArrayList<RecyclerData> recyclerDataArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar();
        recyclerView=findViewById(R.id.screenshotsRecyclerView);
        recyclerDataArrayList=new ArrayList<>();
        //toolBarLayout.setTitle(getTitle());
        Intent intent = getIntent();
        idGame = intent.getIntExtra("idGame", 1020);
        progressBar = findViewById(R.id.progressBar);
        IGamesRepository iGamesRepository = new GamesRepository(getApplication(), this);
        progressBar.setVisibility(View.VISIBLE);
        String query = "fields name, franchises.name, first_release_date, genres.name, total_rating, total_rating_count, cover.url, involved_companies.company.name, platforms.name, summary, screenshots.url; where id = " + idGame + ";";
        iGamesRepository.fetchGames(query, 10000);

        games = new ArrayList<>();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccess(List<GameApiResponse> gamesList, long lastUpdate) {
        progressBar.setVisibility(View.GONE);
        this.games.addAll(gamesList);
        game = games.get(0);

        showGameCover();
        showGameName();
        showReleaseDate();
        showRating();
        showReviewsNumber();
        showGenres();
        showPlatforms();
        setFranchiseButton();
        setCompanyButton();
        GridLayout buttonsLayout = findViewById(R.id.buttonsLayout);
        showWantedButton(buttonsLayout);
        showPlayiedButton(buttonsLayout);
        showDescription();
        showScreenshots();
    }

    private void showScreenshots() {
        if (game.getScreenshots() != null){
            for (int i = 0; i < game.getScreenshots().size(); i++) {
                if (game.getScreenshots() != null)
                    recyclerDataArrayList.add(new RecyclerData(game.getId(), game.getScreenshots().get(i).getUrl()));
            }
            RecyclerScreenshotsViewAdapter adapter=new RecyclerScreenshotsViewAdapter(recyclerDataArrayList,this);
            LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showDescription() {
        TextView descriptionView = findViewById(R.id.descriptionView);
        descriptionView.setVisibility(View.VISIBLE);
        TextView descriptionText = findViewById(R.id.descriptionText);
        descriptionText.setText(game.getDescription());
    }

    private void showWantedButton(GridLayout buttonsLayout){
        Button wantedButton = new Button(this);
        wantedButton.setText(R.string.wanted);
        wantedButton.setTextSize(18);
        wantedButton.setGravity(Gravity.CENTER);
        wantedButton.setBackgroundResource(R.drawable.rounded_corner);
        buttonsLayout.addView(wantedButton);
        wantedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void showPlayiedButton(GridLayout buttonsLayout) {
        Button playButton = new Button(this);
        playButton.setText(R.string.palyied);
        playButton.setTextSize(18);
        playButton.setGravity(Gravity.CENTER);
        playButton.setBackgroundResource(R.drawable.rounded_corner);
        buttonsLayout.addView(playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showReviewsNumber() {
        TextView ratingCount = findViewById(R.id.ratingCount);
        String ratingCountString = "\n" + getString(R.string.reviews);
        ratingCount.setText(game.getRatingCount() + ratingCountString);
    }

    @SuppressLint("SetTextI18n")
    private void showRating() {
        DecimalFormat df = new DecimalFormat("0.0");
        double value = game.getRating() / 10;
        df.setRoundingMode(RoundingMode.DOWN);
        TextView rating = findViewById(R.id.rating);
        String ratingString = "\n" + getString(R.string.rating);
        rating.setText(df.format(value) + ratingString);
    }

    private void showPlatforms() {
        TextView platformsView = findViewById(R.id.platformsView);
        platformsView.setVisibility(View.VISIBLE);
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

    private void showGenres() {
        LinearLayout linearLayout = findViewById(R.id.genresLayout);
        TextView genresView = findViewById(R.id.genresView);
        genresView.setVisibility(View.VISIBLE);
        List<String> genres = game.getGenresString();
        for (String genre : genres) {
            TextView textView = new TextView(this);
            textView.setTextSize(20);
            textView.setPadding(15, 15, 15, 15);
            textView.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.rounded_corner));
            textView.setText(genre);
            textView.setOnClickListener(v -> {
                Intent myIntent = new Intent(getApplicationContext(), GenreActivity.class);
                myIntent.putExtra("genreName", genre);
                startActivity(myIntent);
            });
            linearLayout.addView(textView);
        }
    }

    private void showReleaseDate() {
        TextView releaseDateView = findViewById(R.id.releaseDate);
        String date = "";
        if (game.getReleaseDate() != null)
             date = game.getReleaseDate();
        else
            date = getString(R.string.Unreleased);
        releaseDateView.setText(date);
    }

    private void showGameCover() {
        ImageView gameImage = findViewById(R.id.gameCover);
        String uriString = game.getCover().getUrl();
        String newUri = uriString.replace("thumb", "cover_big_2x");
        Picasso.get().load(newUri).into(gameImage);
    }

    private void showGameName() {
        TextView gameNameView = findViewById(R.id.gameName);
        gameNameView.setText(game.getName());
    }

    private void setFranchiseButton() {
        Button showFranchiseButton = findViewById(R.id.showFranchiseButton);
        showFranchiseButton.setVisibility(View.VISIBLE);
        if (game.getFranchise() != null){
            showFranchiseButton.setText(game.getFranchise().getName());
        }
        showFranchiseButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), FranchiseActivity.class);
            if (game.getFranchise() != null)
                myIntent.putExtra("nameFranchise", game.getFranchise().getName());
            startActivity(myIntent);
        });
    }

    private void setCompanyButton() {
        Button showCompanyButton = findViewById(R.id.showCompanyButton);
        showCompanyButton.setVisibility(View.VISIBLE);
        if (game.getCompanies() != null) {
            showCompanyButton.setText(game.getCompany());
        }
        showCompanyButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), CompanyActivity.class);
            if (game.getCompanies() != null)
                myIntent.putExtra("nameCompany", game.getCompany());
            startActivity(myIntent);
        });
    }

    private void setToolbar() {
        com.example.gamestorm.databinding.ActivityGameBinding binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }
//1020

    @Override
    public void onFailure(String errorMessage) {
        Log.i("onFailure", errorMessage);
    }

    @Override
    public void onGameFavoriteStatusChanged(GameApiResponse game) {

    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (!(fragment instanceof ScreenshotFragment)){
            super.onBackPressed();
        } else {
            CoordinatorLayout coordinatorLayout = findViewById(R.id.scrollView);
            coordinatorLayout.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
            nestedScrollView.setNestedScrollingEnabled(true);
        }
    }
}


