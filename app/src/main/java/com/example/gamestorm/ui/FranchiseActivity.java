package com.example.gamestorm.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gamestorm.adapter.RecyclerData;
import com.example.gamestorm.adapter.RecyclerViewAdapter;
import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.GamesRepository;
import com.example.gamestorm.repository.IGamesRepository;
import com.example.gamestorm.util.ResponseCallback;

import java.util.ArrayList;
import java.util.List;

public class FranchiseActivity extends AppCompatActivity implements ResponseCallback {
    private RecyclerView recyclerView;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_franchise);
        Intent intent = getIntent();
        String franchise = intent.getStringExtra("nameFranchise");
        TextView franchiseTitleView = findViewById(R.id.franchiseTitle);

        progressBar = findViewById(R.id.progressBar);
        recyclerView=findViewById(R.id.franchiseRecyclerView);
        recyclerDataArrayList=new ArrayList<>();
        if (franchise != null) {
            franchiseTitleView.setText(franchise);
            IGamesRepository iGamesRepository = new GamesRepository(getApplication(), this);
            progressBar.setVisibility(View.VISIBLE);
            String query = "fields name, cover.url; where franchises.name = \"" + franchise + "\"; limit 30;";
            iGamesRepository.fetchGames(query,10000);
        }  else {
            franchiseTitleView.setText("No results :(");
        }
    }

    @Override
    public void onSuccess(List<GameApiResponse> gamesList, long lastUpdate) {
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

    @Override
    public void onFailure(String errorMessage) {

    }

    @Override
    public void onGameFavoriteStatusChanged(GameApiResponse game) {

    }
}