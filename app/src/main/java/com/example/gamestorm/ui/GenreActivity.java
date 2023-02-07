package com.example.gamestorm.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.gamestorm.repository.GamesRepository;
import com.example.gamestorm.repository.IGamesRepository;
import com.example.gamestorm.util.ResponseCallback;

import java.util.ArrayList;
import java.util.List;

public class GenreActivity extends AppCompatActivity implements ResponseCallback {
    private RecyclerView recyclerView;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_genre);
        progressBar = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        String genre = intent.getStringExtra("genreName");

        TextView genreTitle = findViewById(R.id.genreTitle);
        genreTitle.setText(genre);

        recyclerView=findViewById(R.id.genreRecyclerView);
        recyclerDataArrayList=new ArrayList<>();

        IGamesRepository gamesRepository = new GamesRepository(getApplication(), this);
        progressBar.setVisibility(View.VISIBLE);
        //1262304000 = 1/1/2010
        String query = "fields name, total_rating, cover.url, genres.name, first_release_date; where genres.name = \"" + genre + "\" & total_rating > 85 & first_release_date > 1262304000; limit 30;";
        checkNetwork();
        gamesRepository.fetchGames(query,0);
    }

    private void checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED &&
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            Toast.makeText(this, R.string.no_connection_message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(List<GameApiResponse> gamesList, int count) {
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
}