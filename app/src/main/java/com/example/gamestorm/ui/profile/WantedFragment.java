package com.example.gamestorm.ui.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gamestorm.adapter.RecyclerData;
import com.example.gamestorm.adapter.RecyclerProfileViewAdapter;
import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.games.IGamesRepository;
import com.example.gamestorm.repository.user.IUserRepository;
import com.example.gamestorm.ui.viewModel.GamesViewModel;
import com.example.gamestorm.ui.viewModel.GamesViewModelFactory;
import com.example.gamestorm.ui.viewModel.UserViewModel;
import com.example.gamestorm.ui.viewModel.UserViewModelFactory;
import com.example.gamestorm.util.Constants;
import com.example.gamestorm.util.ServiceLocator;
import com.example.gamestorm.util.SharedPreferencesUtil;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;


public class WantedFragment extends Fragment {
    private ProgressBar progressBar;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    private GamesViewModel gamesViewModel;
    private UserViewModel userViewModel;
    RecyclerProfileViewAdapter homeAdapter;

    private TextView gamesNumber;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerDataArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wanted, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = requireView().findViewById(R.id.wantedRecyclerView);
        homeAdapter = new RecyclerProfileViewAdapter(recyclerDataArrayList, getContext());
        recyclerView.setAdapter(homeAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        gamesNumber = requireView().findViewById(R.id.wantedNumber);
        progressBar = requireView().findViewById(R.id.progressBar);
        IGamesRepository iGamesRepository;
        try {
            iGamesRepository = ServiceLocator.getInstance().getGamesRepository(requireActivity().getApplication());
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        if (iGamesRepository != null) {
            gamesViewModel = new ViewModelProvider(this, new GamesViewModelFactory(iGamesRepository)).get(GamesViewModel.class);
        }
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void observeViewModel() {
        progressBar.setVisibility(View.VISIBLE);
        sharedPreferencesUtil =
                new SharedPreferencesUtil(requireActivity().getApplication());
        boolean isFirstLoading = sharedPreferencesUtil.readBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                Constants.SHARED_PREFERENCES_FIRST_LOADING_WANTED);

        gamesViewModel.getWantedGames(isFirstLoading).observe(getViewLifecycleOwner(), gameApiResponses -> {
            TextView textView = requireView().findViewById(R.id.noGameText);
            textView.setVisibility(View.GONE);
            if (gameApiResponses.size() == 0){
                textView.setVisibility(View.VISIBLE);
            } else if (gameApiResponses.size() == 1){
                gamesNumber.setText(R.string.one_wanted_game);
            } else {
                gamesNumber.setText(gameApiResponses.size() + " " + getString(R.string.wanted_games));
            }
            recyclerDataArrayList.clear();
            for (GameApiResponse gameApiResponse : gameApiResponses){
                recyclerDataArrayList.add(new RecyclerData(gameApiResponse.getId(),gameApiResponse.getCover().getUrl()));
            }
            homeAdapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            if (isFirstLoading) {
                sharedPreferencesUtil.writeBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                        Constants.SHARED_PREFERENCES_FIRST_LOADING_WANTED, false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
            if (userViewModel.getLoggedUser() != null) {
                observeViewModel();
            }
    }
    private boolean checkNetwork(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}