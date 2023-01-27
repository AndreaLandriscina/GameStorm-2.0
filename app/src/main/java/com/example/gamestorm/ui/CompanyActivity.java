package com.example.gamestorm.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamestorm.adapter.RecyclerData;
import com.example.gamestorm.adapter.RecyclerViewAdapter;
import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.GamesRepository;
import com.example.gamestorm.repository.IGamesRepository;
import com.example.gamestorm.util.ResponseCallback;

import java.util.ArrayList;
import java.util.List;

public class CompanyActivity extends AppCompatActivity implements ResponseCallback {
    private RecyclerView recyclerView;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        Intent intent = getIntent();
        String company = intent.getStringExtra("nameCompany");
        TextView companyTitleView = findViewById(R.id.companyTitle);

        progressBar = findViewById(R.id.progressBar);
        recyclerView=findViewById(R.id.companyRecyclerView);
        recyclerDataArrayList=new ArrayList<>();
        if (company != null){
            companyTitleView.setText(company);
        } else {
            companyTitleView.setText(R.string.no_results);
        }
        IGamesRepository iGamesRepository = new GamesRepository(getApplication(), this);
        progressBar.setVisibility(View.VISIBLE);
        String query = "fields name, cover.url; where involved_companies.company.name = \"" + company + "\"; limit 30;";
        checkNetwork();
        iGamesRepository.fetchGames(query,10000,0);
    }

    private void checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED &&
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this,errorMessage,Toast.LENGTH_LONG).show();
        Log.i("onFailure", errorMessage);
    }

    @Override
    public void onGameFavoriteStatusChanged(GameApiResponse game) {

    }
}