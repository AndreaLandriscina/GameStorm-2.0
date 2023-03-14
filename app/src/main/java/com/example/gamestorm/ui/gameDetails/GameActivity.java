package com.example.gamestorm.ui.gameDetails;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import com.example.gamestorm.adapter.RecyclerData;
import com.example.gamestorm.adapter.RecyclerScreenshotsViewAdapter;
import com.example.gamestorm.adapter.RecyclerViewAdapter;
import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.games.IGamesRepository;
import com.example.gamestorm.repository.user.IUserRepository;
import com.example.gamestorm.ui.GamesViewModel;
import com.example.gamestorm.ui.GamesViewModelFactory;
import com.example.gamestorm.ui.UserViewModel;
import com.example.gamestorm.ui.UserViewModelFactory;
import com.example.gamestorm.ui.profile.LoginActivity;
import com.example.gamestorm.util.Constants;
import com.example.gamestorm.util.ServiceLocator;
import com.example.gamestorm.util.SharedPreferencesUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.math.RoundingMode;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private GameApiResponse game;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    private Button wantedButton;
    private Button playedButton;

    private GamesViewModel gamesViewModel;
    private UserViewModel userViewModel;
    private Calendar today;

    public Button getWantedButton() {
        return wantedButton;
    }

    public Button getPlayedButton() {
        return playedButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbar();

        Intent intent = getIntent();
        int idGame = intent.getIntExtra("idGame", 1020);
        progressBar = findViewById(R.id.progressBar);
        if (checkNetwork()) {
            progressBar.setVisibility(View.VISIBLE);
        }
        IGamesRepository iGamesRepository;
        try {
            iGamesRepository = ServiceLocator.getInstance().getGamesRepository(getApplication());
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        if (iGamesRepository != null) {
            gamesViewModel = new ViewModelProvider(this, new GamesViewModelFactory(iGamesRepository)).get(GamesViewModel.class);
        }

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(getApplication());
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        gamesViewModel.getGame(idGame).observe(this, result -> {
            progressBar.setVisibility(View.GONE);
            game = result;
            onSuccess();
        });
    }

    @SuppressLint("SetTextI18n")

    public void onSuccess() {
        showGameCover();
        showGameName();
        showReleaseDate();
        showRating();
        showReviewsNumber();
        showGenres();
        showPlatforms();
        setFranchiseButton();
        setCompanyButton();
        setButtons();
        setPlayVideoButton();
        showDescription();
        showScreenshots();
        showRelatedGames();
    }

    private void setPlayVideoButton() {
        if (game.getVideoId() == null){
            return;
        }
        Button button = findViewById(R.id.playVideoButton);

        button.setOnClickListener(v -> {
            Log.i("video", game.getVideoId().get("Trailer"));
            if (game.getVideoId().containsKey("Trailer") && game.getVideoId().containsKey("Gameplay video")){
                DialogFragment fragment = new PlayVideoDialogFragment(game.getVideoId());
                fragment.show(getSupportFragmentManager(), "dialog");
            } else {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + game.getVideos().get(0).getVideo_id()));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + game.getVideos().get(0).getVideo_id()));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setButtons() {
        wantedButton = findViewById(R.id.wantedButton);
        playedButton = findViewById(R.id.playedButton);
        wantedButton.setVisibility(View.VISIBLE);
        wantedButton.setText(R.string.wanted);

        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getApplication());
        wantedButton.setText(R.string.wanted);
        playedButton.setVisibility(View.VISIBLE);
        today = Calendar.getInstance();
        if (new Date(game.getFirstReleaseDate() * 1000L).compareTo(today.getTime()) > 0) {
            playedButton.setVisibility(View.GONE);
        }

        setWantedButton(sharedPreferencesUtil);
        setPlayedButton(sharedPreferencesUtil);
    }

    @SuppressLint("SetTextI18n")
    private void setPlayedButton(SharedPreferencesUtil sharedPreferencesUtil) {
        boolean isFirstLoading = sharedPreferencesUtil.readBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                Constants.SHARED_PREFERENCES_FIRST_LOADING_PLAYED);
        gamesViewModel.getPlayedGames(isFirstLoading).observe(this, gameApiResponses -> {
            for (GameApiResponse gameApiResponse : gameApiResponses) {
                if (gameApiResponse.getId() == game.getId() && gameApiResponse.isPlayed()) {
                    this.game.setPlayed(true);
                    playedButton.setText("✓ " + getString(R.string.played));
                    wantedButton.setVisibility(View.GONE);
                    break;
                }
            }
        });
        isFirstLoading = sharedPreferencesUtil.readBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                Constants.SHARED_PREFERENCES_FIRST_LOADING_PLAYING);
        gamesViewModel.getPlayingGames(isFirstLoading).observe(this, gameApiResponses -> {
            for (GameApiResponse gameApiResponse : gameApiResponses) {
                if (gameApiResponse.getId() == game.getId() && gameApiResponse.isPlaying()) {
                    this.game.setPlaying(true);
                    playedButton.setText("✓ " + getString(R.string.playing));
                    wantedButton.setVisibility(View.GONE);
                    break;
                }
            }
        });
        playedButton.setOnClickListener(v -> {
            if (userViewModel.getLoggedUser() != null) {
                DialogFragment dialogFragment;
                if (!game.isPlayed() && !game.isPlaying()) {
                    dialogFragment = new PlayedButtonDialogFragment(gamesViewModel, game);
                } else {
                    dialogFragment = new RemoveGameDialogFragment(this, gamesViewModel, game);
                }
                dialogFragment.show(getSupportFragmentManager(), "Dialog");
            } else {
                goToLoginActivity();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setWantedButton(SharedPreferencesUtil sharedPreferencesUtil) {
        boolean isFirstLoading = sharedPreferencesUtil.readBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                Constants.SHARED_PREFERENCES_FIRST_LOADING_WANTED);
        gamesViewModel.getWantedGames(isFirstLoading).observe(this, gameApiResponses -> {
            if (isFirstLoading) {
                sharedPreferencesUtil.writeBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                        Constants.SHARED_PREFERENCES_FIRST_LOADING_WANTED, false);
            }
            for (GameApiResponse gameApiResponse : gameApiResponses) {
                if (gameApiResponse.getId() == game.getId() && gameApiResponse.isWanted()) {
                    this.game.setWanted(true);
                    wantedButton.setText("✓ " + getString(R.string.wanted));
                    playedButton.setVisibility(View.GONE);
                    break;
                }
            }
        });

        wantedButton.setOnClickListener(v -> {
            if (userViewModel.getLoggedUser() != null) {
                if (game.isWanted()) {
                    game.setWanted(false);
                    gamesViewModel.updateWantedGame(game);
                    wantedButton.setText(R.string.wanted);
                    playedButton.setVisibility(View.VISIBLE);
                } else {
                    game.setWanted(true);
                    game.setAdded(today.getTimeInMillis());
                    gamesViewModel.updateWantedGame(game);
                }
            } else {
                goToLoginActivity();
            }
        });
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void showRelatedGames() {
        gamesViewModel.getSimilarGames(game.getSimilarGames()).observe(this, gameApiResponses -> {
            if (gameApiResponses != null && !gameApiResponses.isEmpty()) {
                TextView textView = findViewById(R.id.relatedView);
                textView.setVisibility(View.VISIBLE);
                recyclerView = findViewById(R.id.relatedRecyclerView);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerDataArrayList = new ArrayList<>();

                for (GameApiResponse gameApiResponse : gameApiResponses) {
                    if (gameApiResponse.getCover() != null)
                        recyclerDataArrayList.add(new RecyclerData(gameApiResponse.getId(), gameApiResponse.getCover().getUrl()));
                }
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(recyclerDataArrayList, this, true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private boolean checkNetwork() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void showScreenshots() {
        recyclerView = findViewById(R.id.screenshotsRecyclerView);
        recyclerDataArrayList = new ArrayList<>();
        Log.i("SCREEN", game.getScreenshots().toString());
        if (game.getScreenshots() != null) {
            for (int i = 0; i < game.getScreenshots().size(); i++) {
                if (game.getScreenshots() != null)
                    recyclerDataArrayList.add(new RecyclerData(game.getId(), game.getScreenshots().get(i).getUrl()));
            }
            RecyclerScreenshotsViewAdapter adapter = new RecyclerScreenshotsViewAdapter(recyclerDataArrayList, this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showDescription() {
        if (game.getDescription() != null) {
            TextView descriptionView = findViewById(R.id.descriptionView);
            descriptionView.setVisibility(View.VISIBLE);
            TextView descriptionText = findViewById(R.id.descriptionText);
            descriptionText.setText(game.getDescription());
        }
    }

    @SuppressLint("SetTextI18n")
    private void showReviewsNumber() {
        TextView ratingCount = findViewById(R.id.ratingCount);
        String ratingCountString = "\n" + getString(R.string.reviews);
        ratingCount.setText(game.getTotalRatingCount() + ratingCountString);
    }

    @SuppressLint("SetTextI18n")
    private void showRating() {
        DecimalFormat df = new DecimalFormat("0.0");
        TextView rating = findViewById(R.id.rating);
        String ratingString = "\n" + getString(R.string.rating);
        if (game.getTotalRating() != 0.0) {
            double value = game.getTotalRating() / 10;
            df.setRoundingMode(RoundingMode.DOWN);
            rating.setText(df.format(value) + ratingString);
        } else {
            rating.setText(getString(R.string.NoRating));
        }
    }

    private void showPlatforms() {
        TextView platformsView = findViewById(R.id.platformsView);
        platformsView.setVisibility(View.VISIBLE);
        TextView platformText = findViewById(R.id.platformText);
        StringBuilder text = new StringBuilder();
        if (game.getPlatforms() != null) {
            List<String> platforms = game.getPlatformsString();
            for (int i = 0; i < platforms.size(); i++) {
                text.append(platforms.get(i));
                if (i != platforms.size() - 1) {
                    text.append(" - ");
                }
            }
        } else {
            text.append(getString(R.string.no_platforms));
        }
        platformText.setText(text);
    }

    private void showGenres() {
        List<String> genres = game.getGenresString();
        LinearLayout linearLayout = findViewById(R.id.genresLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMarginEnd(20);
        TextView genresView = findViewById(R.id.genresView);
        genresView.setVisibility(View.VISIBLE);

        for (String genre : genres) {
            MaterialButton button = new MaterialButton(this);
            button.setLayoutParams(params);
            button.setTextSize(16);
            button.setPadding(15, 15, 15, 15);
            button.setCornerRadius(30);
            button.setText(genre);
            button.setOnClickListener(v -> {
                Intent myIntent = new Intent(getApplicationContext(), GenreActivity.class);
                myIntent.putExtra("genreName", genre);
                startActivity(myIntent);
            });
            linearLayout.addView(button);
        }
    }

    private void showReleaseDate() {
        TextView releaseDateView = findViewById(R.id.releaseDate);
        String date;
        if (game.getFirstReleaseDateString() != null)
            date = game.getFirstReleaseDateString();
        else
            date = getString(R.string.Unreleased);
        releaseDateView.setText(date);
    }

    private void showGameCover() {
        ImageView gameImage = findViewById(R.id.gameCover);
        if (game.getCover() != null) {
            String uriString = game.getCover().getUrl();
            String newUri = uriString.replace("thumb", "cover_big_2x");
            Picasso.get().load(newUri).into(gameImage);
        }
        gameImage.setVisibility(View.VISIBLE);
    }

    private void showGameName() {
        TextView gameNameView = findViewById(R.id.gameName);
        gameNameView.setText(game.getName());
    }

    private void setFranchiseButton() {
        Button showFranchiseButton = findViewById(R.id.showFranchiseButton);
        showFranchiseButton.setVisibility(View.VISIBLE);
        if (game.getFranchise() != null) {
            showFranchiseButton.setText(game.getFirstFranchise().getName());
        }
        showFranchiseButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), FranchiseActivity.class);
            if (game.getFranchise() != null)
                myIntent.putExtra("nameFranchise", game.getFirstFranchise().getName());
            startActivity(myIntent);
        });
    }

    private void setCompanyButton() {
        Button showCompanyButton = findViewById(R.id.showCompanyButton);
        showCompanyButton.setVisibility(View.VISIBLE);
        if (game.getCompanies() != null) {
            showCompanyButton.setText(game.getInvolvedCompany().getCompany().getName());
        }
        showCompanyButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), CompanyActivity.class);
            if (game.getCompanies() != null)
                myIntent.putExtra("nameCompany", game.getInvolvedCompany().getCompany().getName());
            startActivity(myIntent);
        });
    }

    private void setToolbar() {
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.screenshotContainer);
        if (!(fragment instanceof ScreenshotFragment)) {
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

