package com.example.gamestorm.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.example.gamestorm.adapter.RecyclerData;
import com.example.gamestorm.adapter.RecyclerScreenshotsViewAdapter;
import com.example.gamestorm.adapter.RecyclerViewAdapter;
import com.example.gamestorm.databinding.ActivityGameBinding;
import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.GamesRepository;
import com.example.gamestorm.repository.IGamesRepository;
import com.example.gamestorm.util.ResponseCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameActivity extends AppCompatActivity implements ResponseCallback {
    private int idGame;
    private GameApiResponse game;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    private IGamesRepository iGamesRepository;
    private List<String> genres;
    private Button wantedButton;
    private Button playedButton;
    private String loggedUserID = null;

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
        idGame = intent.getIntExtra("idGame", 1020);
        progressBar = findViewById(R.id.progressBar);
        iGamesRepository = new GamesRepository(getApplication(), this);
        progressBar.setVisibility(View.VISIBLE);
        String query = "fields name, franchises.name, first_release_date, genres.name, total_rating, total_rating_count, cover.url, involved_companies.company.name, platforms.name, summary, screenshots.url; where id = " + idGame + ";";

        checkNetwork();
        iGamesRepository.fetchGames(query, 10000, 0);
        genres = new ArrayList<>();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccess(List<GameApiResponse> gamesList, long lastUpdate, int queryNumber) {
        switch (queryNumber) {
            case 0:
                game = gamesList.get(0);
                showGameCover();
                showGameName();
                showReleaseDate();
                showRating();
                showReviewsNumber();
                genres = game.getGenresString();
                showGenres(genres);
                showPlatforms();
                setFranchiseButton();
                setCompanyButton();
                setButtons();
                showDescription();
                showScreenshots();
                getRelatedGames(genres);
                break;
            case 1:
                ArrayList<GameApiResponse> relatedGamesList = selectRelatedGames(gamesList);
                progressBar.setVisibility(View.GONE);
                showRelatedGames(relatedGamesList);
                break;
        }
    }

    private void setButtons() {
        wantedButton = findViewById(R.id.wantedButton);
        playedButton = findViewById(R.id.playedButton);
        DocumentReference docRef = checkLogin();
        //abbiamo il docRef se si è loggati
        if (docRef != null) {
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        wantedButton.setVisibility(View.VISIBLE);
                        playedButton.setVisibility(View.VISIBLE);
                        ArrayList<Integer> desiredGames = (ArrayList<Integer>) document.get("desiredGames");
                        if (desiredGames != null && desiredGames.contains((long) game.getId())) {
                            wantedButton.setText(R.string.alreadyWanted);
                            playedButton.setVisibility(View.GONE);
                        }
                        ArrayList<Integer> playingGames = (ArrayList<Integer>) document.get("playingGames");
                        if (playingGames != null && playingGames.contains((long) game.getId())) {
                            playedButton.setText((R.string.alreadyPlaying));
                            wantedButton.setVisibility(View.GONE);
                        }
                        ArrayList<Integer> playedGames = (ArrayList<Integer>) document.get("playedGames");
                        if (playedGames != null && playedGames.contains((long) game.getId())) {
                            playedButton.setText(R.string.alreadyPlayed);
                            wantedButton.setVisibility(View.GONE);
                        }
                    }
                }
            });
        } else {
            //non è loggato
            wantedButton.setVisibility(View.VISIBLE);
            playedButton.setVisibility(View.VISIBLE);
        }
        wantedButton.setOnClickListener(v -> {
            if (playedButton.getVisibility() != View.GONE) {
                if (docRef != null) {
                    //aggiunge ai giochi desiderati
                    playedButton.setVisibility(View.GONE);
                    docRef.update("desiredGames", FieldValue.arrayUnion(game.getId()));
                } else {
                    goToLoginActivity();
                }
            } else {
                if (docRef != null) {
                    RemoveGameDialogFragment fragment = new RemoveGameDialogFragment(GameActivity.this);
                    goToDialog(fragment);
                } else {
                    goToLoginActivity();
                }
            }
        });
        playedButton.setOnClickListener(v -> {
            if (docRef != null) {
                if (wantedButton.getVisibility() != View.GONE) {
                    PlayedButtonDialogFragment fragment = new PlayedButtonDialogFragment(GameActivity.this);
                    goToDialog(fragment);
                } else {
                    //rimuovo il gioco dai giochi giocati o in gioco
                    RemoveGameDialogFragment fragment = new RemoveGameDialogFragment(GameActivity.this);
                    goToDialog(fragment);
                }
            } else {
                goToLoginActivity();
            }
        });
    }

    private void goToDialog(DialogFragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putInt("idGame", game.getId());
        bundle.putInt("idGame", game.getId());
        bundle.putString("idUser", loggedUserID);
        bundle.putString("gameName", game.getName());
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "Dialog");
    }

    private DocumentReference checkLogin() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = null;
        if (firebaseAuth.getCurrentUser() != null) {
            loggedUserID = firebaseAuth.getCurrentUser().getUid();
        } else if (account != null) {
            loggedUserID = account.getId();
        }
        if (loggedUserID == null) {
            goToLoginActivity();
        } else {
            docRef = firebaseFirestore.collection("User").document(loggedUserID);
        }
        return docRef;
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    //dalla risposta ottenuta bisogna fare una selezione
    private ArrayList<GameApiResponse> selectRelatedGames(List<GameApiResponse> gamesList) {
        int differentGenres;
        ArrayList<GameApiResponse> relatedGamesList = new ArrayList<>();
        for (GameApiResponse gameApiResponse : gamesList) {
            differentGenres = 0;
            //il gioco mostrato non viene messo tra quelli simili
            if (gameApiResponse.getId() != game.getId()) {
                for (int i = 0; i < gameApiResponse.getGenres().size() && differentGenres < 2; i++) {
                    String genre = gameApiResponse.getGenres().get(i).getName();
                    boolean isFound = false;
                    for (int j = 0; j < genres.size() && !isFound; j++) {
                        String genreToFind = genres.get(j);
                        if (genre.equals(genreToFind)) {
                            isFound = true;
                        }
                    }
                    if (!isFound) {
                        differentGenres++;
                    }
                }
                if (differentGenres < 2 && relatedGamesList.size() <= 10) {
                    relatedGamesList.add(gameApiResponse);
                }
            }
        }
        return relatedGamesList;
    }

    private void getRelatedGames(List<String> genres) {
        StringBuilder subquery = new StringBuilder();
        subquery.append("(");

        for (int i = 0; i < genres.size(); i++) {
            subquery.append("\"").append(genres.get(i)).append("\"");
            if (i < genres.size() - 1) {
                subquery.append(", ");
            } else {
                subquery.append(")");
            }
        }
        String query = "fields name, total_rating, cover.url, genres.name, first_release_date; where genres.name = " + subquery + " & total_rating > 80 & first_release_date > 1262304000; limit 30;";
        iGamesRepository.fetchGames(query, 10000, 1);
    }

    private void showRelatedGames(ArrayList<GameApiResponse> relatedGamesList) {
        TextView textView = findViewById(R.id.relatedView);
        textView.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.relatedRecyclerView);
        recyclerDataArrayList = new ArrayList<>();
        if (relatedGamesList != null && !relatedGamesList.isEmpty()) {
            for (GameApiResponse gameApiResponse : relatedGamesList) {
                if (gameApiResponse.getCover() != null)
                    recyclerDataArrayList.add(new RecyclerData(gameApiResponse.getId(), gameApiResponse.getCover().getUrl()));
            }
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(recyclerDataArrayList, this, true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }
    }

    private void checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED &&
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void showScreenshots() {
        recyclerView = findViewById(R.id.screenshotsRecyclerView);
        recyclerDataArrayList = new ArrayList<>();
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
        if (game.getTotalRating() != 0.0){
            double value = game.getTotalRating() / 10;
            df.setRoundingMode(RoundingMode.DOWN);
            rating.setText(df.format(value) + ratingString);
        } else {
            rating.setText(R.string.NoRating);
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
            text.append("No platforms available");
        }
        platformText.setText(text);
    }

    private void showGenres(List<String> genres) {
        LinearLayout linearLayout = findViewById(R.id.genresLayout);
        TextView genresView = findViewById(R.id.genresView);
        genresView.setVisibility(View.VISIBLE);

        for (String genre : genres) {
            TextView textView = new TextView(this);
            textView.setTextSize(20);
            textView.setPadding(15, 15, 15, 15);
            textView.setBackgroundResource(R.drawable.rounded_corner);

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
        String date;
        if (game.getFirstReleaseDate() != null)
            date = game.getFirstReleaseDate();
        else
            date = getString(R.string.Unreleased);
        releaseDateView.setText(date);
    }

    private void showGameCover() {
        ImageView gameImage = findViewById(R.id.gameCover);
        if (game.getCover() != null){
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

    @Override
    public void onFailure(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Log.i("onFailure", errorMessage);
    }

    @Override
    public void onGameFavoriteStatusChanged(GameApiResponse game) {

    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.screeshotContainer);
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


