package com.example.gamestorm.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.GamesRepository;
import com.example.gamestorm.repository.IGamesRepository;
import com.example.gamestorm.databinding.ActivityLoginBinding;
import com.example.gamestorm.databinding.FragmentPlayingBinding;
import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.repository.GamesRepository;
import com.example.gamestorm.repository.IGamesRepository;
import com.example.gamestorm.util.ResponseCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PlayingFragment extends Fragment {

    FragmentPlayingBinding binding;
    FirebaseAuth firebaseAuth;
    Button loginButton;
    ConstraintLayout function_not_available_layout;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private RecyclerView recyclerView;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    private ProgressBar progressBar;
    String loggedUserID;
    ArrayList<Integer> playingGames;
    FirebaseFirestore firebaseFirestore;
    ConstraintLayout playing_games_layout;

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
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getContext(),gso);
        firebaseFirestore=FirebaseFirestore.getInstance();
        playing_games_layout = requireView().findViewById(R.id.playing_games_layout);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

        if(firebaseAuth.getCurrentUser()==null && account == null){
            function_not_available_layout.setVisibility(View.VISIBLE);
            playing_games_layout.setVisibility(View.GONE);
        }else{
            function_not_available_layout.setVisibility(View.GONE);
            if (firebaseAuth.getCurrentUser()!=null){
                loggedUserID=firebaseAuth.getCurrentUser().getUid();
            } else if(account!=null){
                loggedUserID= account.getId();
            }
            function_not_available_layout.setVisibility(View.GONE);
            playing_games_layout.setVisibility(View.VISIBLE);

            //VISUALIZZAZIONE GIOCHI
            DocumentReference docRef = firebaseFirestore.collection("User").document(loggedUserID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            playingGames = (ArrayList<Integer>) document.get("playingGames");

                            //RICERCA GIOCHI IN LISTA
                            if (playingGames != null){
                                Integer gameID;
                                for (int j = 0; j < playingGames.size(); j++) {
                                    gameID = Integer.parseInt(String.valueOf(playingGames.get(j)));

                                    progressBar = requireView().findViewById(R.id.progressBar);
                                    recyclerView = requireView().findViewById(R.id.playingRecyclerView);
                                    recyclerDataArrayList = new ArrayList<>();

                                    IGamesRepository iGamesRepository = new GamesRepository(getActivity().getApplication(),
                                            new ResponseCallback() {
                                                @Override
                                                public void onSuccess(List<GameApiResponse> gamesList, long lastUpdate, int count) {
                                                    progressBar.setVisibility(View.GONE);
                                                    for (GameApiResponse gameApiResponse : gamesList) {
                                                        if (gameApiResponse.getCover() != null)
                                                            recyclerDataArrayList.add(new RecyclerData(gameApiResponse.getId(), gameApiResponse.getCover().getUrl()));
                                                    }
                                                    RecyclerViewAdapter adapter=new RecyclerViewAdapter(recyclerDataArrayList,getContext());
                                                    GridLayoutManager layoutManager=new GridLayoutManager(getContext(),3);
                                                    recyclerView.setLayoutManager(layoutManager);
                                                    recyclerView.setAdapter(adapter);
                                                }

                                                @Override
                                                public void onFailure(String errorMessage) {

                                                }

                                                @Override
                                                public void onGameFavoriteStatusChanged(GameApiResponse game) {

                                                }
                                            });
                                    progressBar.setVisibility(View.VISIBLE);
                                    String query = "fields name, cover.url; where id = " + gameID + "; limit 30;";
                                    iGamesRepository.fetchGames(query, 10000, 0);
                                }
                            }
                        } else {
                            Log.d("LOGGER", "No such document");
                        }
                    } else {
                        Log.d("LOGGER", "get failed with ", task.getException());
                    }
                }
            });


        }

        loginButton.setOnClickListener(view1 -> {
            Intent myIntent = new Intent(getContext(), LoginActivity.class);
            requireActivity().startActivity(myIntent);
        });
    }
}