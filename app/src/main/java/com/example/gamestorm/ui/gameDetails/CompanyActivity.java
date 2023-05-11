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
import com.example.gamestorm.ui.viewModel.GamesViewModel;
import com.example.gamestorm.ui.viewModel.GamesViewModelFactory;
import com.example.gamestorm.util.ServiceLocator;
import com.example.gamestorm.util.sort.SortByAlphabet;
import com.example.gamestorm.util.sort.SortByBestRating;
import com.example.gamestorm.util.sort.SortByMostPopular;
import com.example.gamestorm.util.sort.SortByMostRecent;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompanyActivity extends AppCompatActivity {
    private ArrayList<RecyclerData> recyclerDataArrayList;
    private ProgressBar progressBar;
    private GamesViewModel gamesViewModel;
    private MaterialButton sorting;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_company);

        Intent intent = getIntent();
        String company = intent.getStringExtra("nameCompany");
        TextView companyTitleView = findViewById(R.id.companyTitle);

        progressBar = findViewById(R.id.progressBar);
        RecyclerView recyclerView = findViewById(R.id.companyRecyclerView);
        sorting = findViewById(R.id.sorting);

        recyclerDataArrayList=new ArrayList<>();
        adapter = new RecyclerViewAdapter(recyclerDataArrayList,this, false);
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if (company != null){
            progressBar.setVisibility(View.VISIBLE);
            companyTitleView.setText(company);
        } else {
            sorting.setVisibility(View.GONE);
            companyTitleView.setText(R.string.no_results);
        }
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

        gamesViewModel.getCompanyGames(company).observe(this, result -> {
            progressBar.setVisibility(View.GONE);
            Collections.sort(result, new SortByMostRecent());
            showGames(result);
            setSorting(result);
        });

    }
    private int lastSelectedSortingParameter = 1;
    private void setSorting(List<GameApiResponse> games) {
        sorting.setOnClickListener(v -> {
            final String[] listItems = getResources().getStringArray(R.array.sorting_parameters);

            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.sort_by_dialog_title)
                    .setSingleChoiceItems(listItems, lastSelectedSortingParameter, (dialog, i) -> {
                        String sortingParameter = listItems[i];
                        lastSelectedSortingParameter = i;
                        if (!games.isEmpty()) {
                            sortGames(games, sortingParameter);
                        } else {
                            Toast.makeText(this, R.string.no_connection_message, Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }).setNegativeButton(R.string.cancel_text, (dialogInterface, i) -> dialogInterface.dismiss()).show();
        });
    }
    private boolean checkNetwork() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void showGames(List<GameApiResponse> gamesList) {
        progressBar.setVisibility(View.GONE);
        recyclerDataArrayList.clear();
        for (GameApiResponse gameApiResponse : gamesList) {
            if (gameApiResponse.getCover() != null)
                recyclerDataArrayList.add(new RecyclerData(gameApiResponse.getId(), gameApiResponse.getCover().getUrl()));
        }
        adapter.notifyDataSetChanged();
    }

    public void sortGames(List<GameApiResponse> games, String sortingParameter) {
        switch (sortingParameter) {
            case "Most popular":
            case "Più popolare":
                Collections.sort(games, new SortByMostPopular());
                break;
            case "Most recent":
            case "Più recente":
                Collections.sort(games, new SortByMostRecent());
                break;
            case "Best rating":
            case "Voto migliore":
                Collections.sort(games, new SortByBestRating());
                break;
            case "Alphabet":
            case "Alfabeto":
                Collections.sort(games, new SortByAlphabet());
                break;
        }
        showGames(games);
    }
}