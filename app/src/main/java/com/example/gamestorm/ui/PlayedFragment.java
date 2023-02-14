package com.example.gamestorm.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.gamestorm.adapter.RecyclerData;
import com.example.gamestorm.adapter.RecyclerProfileViewAdapter;
import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.GamesRepository;
import com.example.gamestorm.repository.IGamesRepository;
import com.example.gamestorm.databinding.FragmentPlayedBinding;
import com.example.gamestorm.util.ResponseCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayedFragment extends Fragment implements ResponseCallback{

    FragmentPlayedBinding binding;
    FirebaseAuth firebaseAuth;
    Button loginButton;
    ConstraintLayout function_not_available_layout;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private RecyclerView recyclerView;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    private ProgressBar progressBar;
    String loggedUserID;
    ArrayList<Integer> playedGames;
    FirebaseFirestore firebaseFirestore;
    ConstraintLayout played_games_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_played, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentPlayedBinding.inflate(getLayoutInflater());
        firebaseAuth = FirebaseAuth.getInstance();
        function_not_available_layout = requireView().findViewById(R.id.function_not_available_layout);
        loginButton = requireView().findViewById(R.id.loginButton);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("1091326442567-dbkvi0h9877eego2ou819bepnb05h65g.apps.googleusercontent.com").requestEmail().build();
        gsc = GoogleSignIn.getClient(requireContext(), gso);
        firebaseFirestore = FirebaseFirestore.getInstance();
        played_games_layout = requireView().findViewById(R.id.played_games_layout);
        progressBar = requireView().findViewById(R.id.progressBar);
        recyclerView = requireView().findViewById(R.id.playedRecyclerView);

        if (!isLogged()) {
            function_not_available_layout.setVisibility(View.VISIBLE);
            played_games_layout.setVisibility(View.GONE);
        } else {
            function_not_available_layout.setVisibility(View.GONE);
            played_games_layout.setVisibility(View.VISIBLE);
        }
        loginButton.setOnClickListener(view1 -> Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_loginActivity));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNetworkAvailable(requireContext())) {
            if (isLogged()) {
                viewGames();
            }
        } else {
            Toast.makeText(requireContext(), R.string.no_connection_message, Toast.LENGTH_LONG).show();
        }
    }

    private void viewGames() {
        DocumentReference docRef = firebaseFirestore.collection("User").document(loggedUserID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    Log.i("LOGGER", "arr " + Objects.requireNonNull(document.get("playedGames")));
                    playedGames = (ArrayList<Integer>) document.get("playedGames");

                    //RICERCA GIOCHI IN LISTA
                    assert playedGames != null;
                    if (!playedGames.isEmpty()) {
                        ArrayList<Integer> idGames = new ArrayList<>();
                        for (int j = 0; j < playedGames.size(); j++) {
                            idGames.add(Integer.parseInt(String.valueOf(playedGames.get(j))));
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        StringBuilder subquery = new StringBuilder();
                        subquery.append("(");

                        for (int i = 0; i < idGames.size(); i++) {
                            subquery.append(idGames.get(i));
                            if (i < idGames.size() - 1) {
                                subquery.append(", ");
                            } else {
                                subquery.append(")");
                            }
                        }
                        String query = "fields name, cover.url; where id = " + subquery + "; limit 30;";
                        Log.i("query", query);
                        IGamesRepository iGamesRepository = new GamesRepository(requireActivity().getApplication(), this);
                        iGamesRepository.fetchGames(query, 0);
                    } else {
                        recyclerView = requireView().findViewById(R.id.playedRecyclerView);
                        recyclerDataArrayList = new ArrayList<>();
                        RecyclerProfileViewAdapter adapter = new RecyclerProfileViewAdapter(recyclerDataArrayList, getContext());
                        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    Log.d("LOGGER", "No such document");
                }
            } else {
                Log.d("LOGGER", "get failed with ", task.getException());
            }
        });
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private boolean isLogged() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (firebaseAuth.getCurrentUser() == null && account == null) {
            return false;
        } else {
            if (firebaseAuth.getCurrentUser() != null) {
                loggedUserID = firebaseAuth.getCurrentUser().getUid();
            } else if (account != null) {
                loggedUserID = account.getId();
            }
            return true;
        }
    }

    @Override
    public void onSuccess(List<GameApiResponse> gamesList, int count) {
        progressBar.setVisibility(View.GONE);
        recyclerDataArrayList = new ArrayList<>();
        for (GameApiResponse gameApiResponse : gamesList) {
            if (gameApiResponse.getCover() != null)
                recyclerDataArrayList.add(new RecyclerData(gameApiResponse.getId(), gameApiResponse.getCover().getUrl()));
        }
        RecyclerProfileViewAdapter adapter = new RecyclerProfileViewAdapter(recyclerDataArrayList, getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(String errorMessage) {
        Log.i("E", errorMessage);
    }
}