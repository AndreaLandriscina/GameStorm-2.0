package com.example.gamestorm.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.gamestorm.Adapter.RecyclerData;
import com.example.gamestorm.Adapter.RecyclerViewAdapter;
import com.example.gamestorm.Model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.Repository.GamesRepository;
import com.example.gamestorm.Repository.IGamesRepository;
import com.example.gamestorm.util.ResponseCallback;

import java.util.ArrayList;
import java.util.List;

public class FranchiseActivity extends AppCompatActivity implements ResponseCallback {
    private RecyclerView recyclerView;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    private IGamesRepository iGamesRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.franchise_saga);
        Intent intent = getIntent();
        String franchise = intent.getStringExtra("nameFranchise");

        recyclerView=findViewById(R.id.idCourseRV);
        recyclerDataArrayList=new ArrayList<>();
        iGamesRepository = new GamesRepository(getApplication(), this);
        String query = "fields name, cover.url; where franchises.name = \"" + franchise + "\"; limit 30;";
        iGamesRepository.fetchGames(query,10000);
    }

    @Override
    public void onSuccess(List<GameApiResponse> gamesList, long lastUpdate) {
        for (GameApiResponse gameApiResponse : gamesList) {
            if (gameApiResponse.getCover() != null)
                recyclerDataArrayList.add(new RecyclerData(gameApiResponse.getCover().getUrl()));

        }
        RecyclerViewAdapter adapter=new RecyclerViewAdapter(recyclerDataArrayList,this);
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