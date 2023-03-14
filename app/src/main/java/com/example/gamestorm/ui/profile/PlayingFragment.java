package com.example.gamestorm.ui.profile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.gamestorm.adapter.RecyclerData;
import com.example.gamestorm.adapter.RecyclerProfileViewAdapter;
import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.databinding.FragmentPlayingBinding;
import com.example.gamestorm.repository.games.IGamesRepository;
import com.example.gamestorm.repository.user.IUserRepository;
import com.example.gamestorm.ui.GamesViewModel;
import com.example.gamestorm.ui.GamesViewModelFactory;
import com.example.gamestorm.ui.UserViewModel;
import com.example.gamestorm.ui.UserViewModelFactory;
import com.example.gamestorm.util.Constants;
import com.example.gamestorm.util.ServiceLocator;
import com.example.gamestorm.util.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class PlayingFragment extends Fragment{

    FragmentPlayingBinding binding;
    FirebaseAuth firebaseAuth;
    Button loginButton;
    ConstraintLayout function_not_available_layout;
    ArrayList<Integer> playingGames;
    ConstraintLayout playing_games_layout;
    private GamesViewModel gamesViewModel;
    private UserViewModel userViewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private boolean isFirstLoading;
    RecyclerProfileViewAdapter homeAdapter;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    IGamesRepository iGamesRepository;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=FragmentPlayingBinding.inflate(getLayoutInflater());
        firebaseAuth=FirebaseAuth.getInstance();
        function_not_available_layout=requireView().findViewById(R.id.function_not_available_layout);
        loginButton=requireView().findViewById(R.id.loginButton);
        playing_games_layout = requireView().findViewById(R.id.playing_games_layout);
        RecyclerView recyclerView = requireView().findViewById(R.id.playingRecyclerView);
        ProgressBar progressBar = requireView().findViewById(R.id.progressBar);
        recyclerDataArrayList = new ArrayList<>();
        homeAdapter = new RecyclerProfileViewAdapter(recyclerDataArrayList, getContext());
        recyclerView.setAdapter(homeAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);


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

        if (userViewModel.getLoggedUser() == null) {
            function_not_available_layout.setVisibility(View.VISIBLE);
            playing_games_layout.setVisibility(View.GONE);
            loginButton.setOnClickListener(view1 -> Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_loginActivity));
        } else {
            function_not_available_layout.setVisibility(View.GONE);
            playing_games_layout.setVisibility(View.VISIBLE);
        }

    }
    private void observeViewModel() {
        sharedPreferencesUtil =
                new SharedPreferencesUtil(requireActivity().getApplication());
        isFirstLoading = sharedPreferencesUtil.readBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                Constants.SHARED_PREFERENCES_FIRST_LOADING_PLAYING);
        playingGames = new ArrayList<>();

        gamesViewModel.getPlayingGames(isFirstLoading).observe(getViewLifecycleOwner(), gameApiResponses -> {
            LinearLayout layout = requireView().findViewById(R.id.noGameText);
            layout.setVisibility(View.GONE);
            if (gameApiResponses.isEmpty()){
                layout.setVisibility(View.VISIBLE);
            }
            recyclerDataArrayList.clear();
            for (GameApiResponse gameApiResponse : gameApiResponses){

                recyclerDataArrayList.add(new RecyclerData(gameApiResponse.getId(),gameApiResponse.getCover().getUrl()));
            }
            homeAdapter.notifyDataSetChanged();



            if (isFirstLoading) {
                sharedPreferencesUtil.writeBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                        Constants.SHARED_PREFERENCES_FIRST_LOADING_PLAYING, false);
            }

        });
    }
    @Override
    public void onResume() {
        super.onResume();

            if (userViewModel.getLoggedUser() != null) {
                function_not_available_layout.setVisibility(View.GONE);
                playing_games_layout.setVisibility(View.VISIBLE);
                observeViewModel();
            } else {
                function_not_available_layout.setVisibility(View.VISIBLE);
                playing_games_layout.setVisibility(View.GONE);
            }

    }
    

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    
}