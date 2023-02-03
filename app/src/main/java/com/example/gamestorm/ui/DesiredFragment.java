package com.example.gamestorm.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.model.UserModel;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.GamesRepository;
import com.example.gamestorm.repository.IGamesRepository;
import com.example.gamestorm.databinding.FragmentDesiredBinding;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;


public class DesiredFragment extends Fragment {

    FragmentDesiredBinding binding;
    FirebaseAuth firebaseAuth;
    Button loginButton;
    ConstraintLayout function_not_available_layout;
    ConstraintLayout desired_games_layout;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private RecyclerView recyclerView;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    private ProgressBar progressBar;
    String loggedUserID;
    private ArrayList<Integer> desiredGames;
    FirebaseFirestore firebaseFirestore;
    public static String desiredTag;
    public static View desiredContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_desired, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentDesiredBinding.inflate(getLayoutInflater());
        function_not_available_layout = requireView().findViewById(R.id.function_not_available_layout);
        desired_games_layout = requireView().findViewById(R.id.desired_games_layout);
        loginButton = requireView().findViewById(R.id.loginButton);


        if (!isLogged()) {
            function_not_available_layout.setVisibility(View.VISIBLE);
            desired_games_layout.setVisibility(View.GONE);
        } else {
            function_not_available_layout.setVisibility(View.GONE);
            desired_games_layout.setVisibility(View.VISIBLE);

            //VISUALIZZAZIONE GIOCHI
            if(isNetworkAvailable(getContext())) {
                    //viewGames();
                Log.i("LOGGER","oncreateview");
            }  else{
                Snackbar.make(view.findViewById(R.id.Coordinatorlyt), R.string.no_connection_message, Snackbar.LENGTH_LONG).show();
            }

        }

        loginButton.setOnClickListener(view1 -> {
        Intent myIntent = new Intent(getContext(), LoginActivity.class);
        requireActivity().startActivity(myIntent);
    });
}

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isNetworkAvailable(getContext())) {
            if (isLogged()) {
                viewGames();
            }
        }else{
            Snackbar.make(requireView().findViewById(R.id.Coordinatorlyt), R.string.no_connection_message, Snackbar.LENGTH_LONG).show();
        }
    }

    private void viewGames(){
        firebaseFirestore=FirebaseFirestore.getInstance();
        DocumentReference docRef = firebaseFirestore.collection("User").document(loggedUserID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        desiredGames = (ArrayList<Integer>) document.get("desiredGames");
                        //Log.i("LOGGER","desired games: "+desiredGames.toString());
                        //RICERCA GIOCHI IN LISTA
                        if (!desiredGames.isEmpty()){
                            Log.i("LOGGER","desired games: "+desiredGames.toString());
                            Integer gameID;
                            for (int j = 0; j < desiredGames.size(); j++) {
                                gameID = Integer.parseInt(String.valueOf(desiredGames.get(j)));
                                Log.i("LOGGER","gameid "+gameID);

                                progressBar = requireView().findViewById(R.id.progressBar);
                                recyclerView = requireView().findViewById(R.id.desiredRecyclerView);
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
                                                adapter.notifyDataSetChanged();
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
                        } else {
                            recyclerView = requireView().findViewById(R.id.desiredRecyclerView);
                            recyclerDataArrayList = new ArrayList<>();
                            RecyclerViewAdapter adapter=new RecyclerViewAdapter(recyclerDataArrayList,getContext());
                            GridLayoutManager layoutManager=new GridLayoutManager(getContext(),3);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);
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

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private boolean isLogged(){
        firebaseAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("1091326442567-dbkvi0h9877eego2ou819bepnb05h65g.apps.googleusercontent.com").requestEmail().build();
        //gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getContext(), gso);
        firebaseFirestore=FirebaseFirestore.getInstance();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
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

}