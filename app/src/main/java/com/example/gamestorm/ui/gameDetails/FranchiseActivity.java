package com.example.gamestorm.ui.gameDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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

import com.example.gamestorm.adapter.RecyclerData;
import com.example.gamestorm.adapter.RecyclerViewAdapter;
import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.games.IGamesRepository;
import com.example.gamestorm.ui.GamesViewModel;
import com.example.gamestorm.ui.GamesViewModelFactory;
import com.example.gamestorm.util.ServiceLocator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class FranchiseActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    private ProgressBar progressBar;
    private GamesViewModel gamesViewModel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_franchise);
        Intent intent = getIntent();
        String franchise = intent.getStringExtra("nameFranchise");
        TextView franchiseTitleView = findViewById(R.id.franchiseTitle);

        progressBar = findViewById(R.id.progressBar);
        recyclerView=findViewById(R.id.franchiseRecyclerView);
        recyclerDataArrayList=new ArrayList<>();
        if (franchise != null) {
            franchiseTitleView.setText(franchise);
            if (checkNetwork()) {
                progressBar.setVisibility(View.VISIBLE);
                IGamesRepository iGamesRepository;
                try {
                    iGamesRepository = ServiceLocator.getInstance().getGamesRepository(getApplication());
                } catch (GeneralSecurityException | IOException e) {
                    throw new RuntimeException(e);
                }

                if (iGamesRepository != null) {
                    gamesViewModel = new ViewModelProvider(this, new GamesViewModelFactory(iGamesRepository)).get(GamesViewModel.class);
                }
                gamesViewModel.getFranchiseGames(franchise).observe(this, result -> {
                    progressBar.setVisibility(View.GONE);
                    onSuccess(result);
                });
            }
        }  else {
            franchiseTitleView.setText(R.string.no_results);
        }

    }

    private boolean checkNetwork() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (!activeNetworkInfo.isConnected()){
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void onSuccess(List<GameApiResponse> gamesList) {
        progressBar.setVisibility(View.GONE);
        for (GameApiResponse gameApiResponse : gamesList) {
            if (gameApiResponse.getCover() != null)
                recyclerDataArrayList.add(new RecyclerData(gameApiResponse.getId(), gameApiResponse.getCover().getUrl()));
        }
        RecyclerViewAdapter adapter=new RecyclerViewAdapter(recyclerDataArrayList,this, false);
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}