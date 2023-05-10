package com.example.gamestorm.ui.home;


import static com.example.gamestorm.util.Constants.LAST_UPDATE_HOME;
import static com.example.gamestorm.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gamestorm.adapter.HomeAdapter;
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
import com.google.android.material.textview.MaterialTextView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private RecyclerView galleryPopular;
    private RecyclerView galleryBestGames;
    private RecyclerView galleryForYou;
    private RecyclerView galleryLatestReleases;
    private RecyclerView galleryIncoming;

    private TextView loginTextView;

    LayoutInflater inflater;

    private GamesViewModel gamesViewModel;

    private UserViewModel userViewModel;
    NestedScrollView scrollView;
    private SharedPreferencesUtil sharedPreferencesUtil;

    public HomeFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        IGamesRepository iGamesRepository;
        try {
            iGamesRepository = ServiceLocator.getInstance().getGamesRepository(requireActivity().getApplication());
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        if (iGamesRepository != null) {
            gamesViewModel = new ViewModelProvider(this, new GamesViewModelFactory(iGamesRepository)).get(GamesViewModel.class);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflater = LayoutInflater.from(getContext());
        scrollView = requireView().findViewById(R.id.homeFragment);
        scrollView.setVisibility(View.GONE);

        loginTextView = view.findViewById(R.id.textViewLoginHomePage);
        Button loginButton = view.findViewById(R.id.buttonLoginHomePage);

        galleryPopular = view.findViewById(R.id.homeGalleryPopular);
        galleryBestGames = view.findViewById(R.id.homeGalleryBestGames);
        galleryForYou = view.findViewById(R.id.homeGalleryForYou);
        galleryLatestReleases = view.findViewById(R.id.homeGalleryLatestReleases);
        galleryIncoming = view.findViewById(R.id.homeGalleryIncoming);

        setShowAll();

        galleryForYou.setVisibility(View.VISIBLE);
        loginTextView.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        //LOGIN
        if (!isLogged()) {
            galleryForYou.setVisibility(View.GONE);
            loginTextView.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            loginButton.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_loginActivity));
        }

        sharedPreferencesUtil = new SharedPreferencesUtil(requireActivity().getApplication());
        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(
                SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE_HOME) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(
                    SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE_HOME);
        }
        observeViewModel(lastUpdate);
    }

    private void setShowAll() {
        MaterialTextView showAllPopular = requireView().findViewById(R.id.showAllPopular);
        showAllPopular.setOnClickListener(v -> {
            Intent myIntent = new Intent(requireContext(), AllGamesActivity.class);
            myIntent.putExtra("section", "POPULAR");
            startActivity(myIntent);
        });
        MaterialTextView showAllBest = requireView().findViewById(R.id.showAllBest);
        showAllBest.setOnClickListener(v -> {
            Intent myIntent = new Intent(requireContext(), AllGamesActivity.class);
            myIntent.putExtra("section", "BEST");
            startActivity(myIntent);
        });
        MaterialTextView showAllLatest = requireView().findViewById(R.id.showAllLatest);
        showAllLatest.setOnClickListener(v -> {
            Intent myIntent = new Intent(requireContext(), AllGamesActivity.class);
            myIntent.putExtra("section", "LATEST");
            startActivity(myIntent);
        });
        MaterialTextView showAllIncoming = requireView().findViewById(R.id.showAllIncoming);
        showAllIncoming.setOnClickListener(v -> {
            Intent myIntent = new Intent(requireContext(), AllGamesActivity.class);
            myIntent.putExtra("section", "INCOMING");
            startActivity(myIntent);
        });
    }

    private void observeViewModel(String lastUpdate) {
        gamesViewModel.getPopularGames(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> showGames(0, result));
        gamesViewModel.getBestGames(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> showGames(1, result));
        gamesViewModel.getLatestGames(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> showGames(2, result));
        gamesViewModel.getIncomingGames(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> showGames(3, result));
    }

    public void showGames(int countQuery, List<GameApiResponse> gameList) {
        switch (countQuery) {
            case 0:
                showPopular(gameList);
                break;
            case 1:
                showBest(gameList);
                break;
            case 2:
                showLatest(gameList);
                break;
            case 3:
                showIncoming(gameList);
                break;
            case 4:
                showForYou(gameList);
                break;
        }
        LinearLayout layout = requireView().findViewById(R.id.icon);
        layout.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
    }

    private void showPopular(List<GameApiResponse> gameList) {
        showInRecyclerView(gameList, galleryPopular);
    }

    private void showInRecyclerView(List<GameApiResponse> gameList, RecyclerView recyclerView) {
        List<GameApiResponse> newList = new ArrayList<>();
        for (GameApiResponse gameApiResponse : gameList) {
            if (gameApiResponse.getCover() != null) {
                newList.add(gameApiResponse);
            }
        }
        HomeAdapter homeAdapter = new HomeAdapter(newList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(homeAdapter);
    }

    private void showForYou(List<GameApiResponse> gameList) {
        if (gameList != null && !gameList.isEmpty()) {
            galleryForYou.setVisibility(View.VISIBLE);
            loginTextView.setVisibility(View.GONE);
            showInRecyclerView(gameList, galleryForYou);
        }
    }

    private void showBest(List<GameApiResponse> gameList) {
        showInRecyclerView(gameList, galleryBestGames);
    }

    private void showIncoming(List<GameApiResponse> gameList) {
        showInRecyclerView(gameList, galleryIncoming);
    }

    private void showLatest(List<GameApiResponse> gameList) {
        showInRecyclerView(gameList, galleryLatestReleases);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private boolean isLogged() {
        return userViewModel.getLoggedUser() != null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isLogged()) {
            loginTextView.setVisibility(View.VISIBLE);
            loginTextView.setText(R.string.loginPlayedGame);
            galleryForYou.setVisibility(View.GONE);
            //per ottenere i giochi personali c'è bisogno di prendere i giochi giocati
            boolean isFirstLoading = sharedPreferencesUtil.readBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                    Constants.SHARED_PREFERENCES_FIRST_LOADING_PLAYED);
            gamesViewModel.getPlayedGames(isFirstLoading).observe(getViewLifecycleOwner(), played ->
                    gamesViewModel.getForYouGames(Long.parseLong("0")).observe(getViewLifecycleOwner(), forYou -> {
                        for (GameApiResponse game : played){
                            forYou.remove(game);
                        }
                        showGames(4, forYou);
                    }));
            gamesViewModel.getPlayedGames(isFirstLoading).observe(getViewLifecycleOwner(), gameApiResponses -> {});
        }
    }

}