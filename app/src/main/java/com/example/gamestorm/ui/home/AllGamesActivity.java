package com.example.gamestorm.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamestorm.R;
import com.example.gamestorm.adapter.RecyclerData;
import com.example.gamestorm.adapter.RecyclerProfileViewAdapter;
import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.repository.games.IGamesRepository;
import com.example.gamestorm.ui.viewModel.GamesViewModel;
import com.example.gamestorm.ui.viewModel.GamesViewModelFactory;
import com.example.gamestorm.util.ServiceLocator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class AllGamesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    private ProgressBar progressBar;
    private GamesViewModel gamesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_all_games);

        Intent intent = getIntent();
        String section = intent.getStringExtra("section");
        TextView titleView = findViewById(R.id.allGamesTitle);

        progressBar = findViewById(R.id.progressBarr);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerDataArrayList = new ArrayList<>();

        if (checkNetwork()) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            titleView.setText(R.string.no_connection);
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
        switch (section) {
            case "POPULAR" :
                gamesViewModel.getAllPopularGames().observe(this, result -> {
                    titleView.setText(R.string.popular);
                    onSuccess(result);
                });
                break;
            case "BEST" :
                gamesViewModel.getAllBestGames().observe(this, result -> {
                    titleView.setText(R.string.bestgames);
                    onSuccess(result);
                });
                break;
            case "LATEST" :
                gamesViewModel.getAllLatestGames().observe(this, result -> {
                    titleView.setText(R.string.latestreleases);
                    onSuccess(result);
                });
                break;
            case "INCOMING" :
                gamesViewModel.getAllIncomingGames().observe(this, result -> {
                    titleView.setText(R.string.incoming);
                    onSuccess(result);
                });
                break;
        }
    }

    private boolean checkNetwork() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void onSuccess(List<GameApiResponse> gamesList) {
        progressBar.setVisibility(View.GONE);
        for (GameApiResponse gameApiResponse : gamesList) {
            if (gameApiResponse.getCover() != null)
                recyclerDataArrayList.add(new RecyclerData(gameApiResponse.getId(), gameApiResponse.getCover().getUrl()));
        }
        RecyclerProfileViewAdapter adapter = new RecyclerProfileViewAdapter(recyclerDataArrayList, this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
