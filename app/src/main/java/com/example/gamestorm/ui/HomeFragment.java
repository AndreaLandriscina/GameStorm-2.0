package com.example.gamestorm.ui;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamestorm.database.TinyDB;
import com.example.gamestorm.model.Cover;
import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.GamesRepository;
import com.example.gamestorm.repository.IGamesRepository;
import com.example.gamestorm.util.ResponseCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class HomeFragment extends Fragment implements ResponseCallback {
    private LinearLayout galleryPopular;
    private LinearLayout galleryBestGames;
    private LinearLayout galleryForYou;
    private LinearLayout galleryLatestReleases;
    private LinearLayout galleryIncoming;

    private TextView loginTextView;
    private Button loginButton;
    private HorizontalScrollView forYouScrollView;

    private ProgressBar progressBar;

    LayoutInflater inflater;

    private IGamesRepository iGamesRepository;
    private List<GameApiResponse> gamesPopular;
    private List<GameApiResponse> gamesBest;
    private List<GameApiResponse> gamesForYou;
    private List<GameApiResponse> gamesLatestReleases;
    private List<GameApiResponse> gamesIncoming;

    private GameApiResponse game;
    String queryPopular;
    String queryForYou;
    String queryLatestReleases;
    String queryIncoming;
    String queryBestGames;

    //LOGIN
    FirebaseAuth firebaseAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FirebaseFirestore firebaseFirestore;
    String loggedUserID;

    //SHAREDPREFERENCE
    TinyDB tinydb;

    public HomeFragment() {

    }
    private long currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis()/1000;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tinydb = new TinyDB(requireActivity().getApplication());

        iGamesRepository = new GamesRepository(requireActivity().getApplication(), this);
        queryPopular = "fields id, name, cover.url;where follows!=null; sort follows desc; limit 30;";
        queryLatestReleases = "fields id, name, cover.url; sort first_release_date desc; limit 30;";
        queryIncoming = "fields id, name, cover.url; where first_release_date > " +Long.toString(currentDate())+";sort first_release_date asc; limit 30;";
        queryBestGames="fields id, name, cover.url;where total_rating_count>1000;sort total_rating desc;limit 30;";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflater = LayoutInflater.from(getContext());

        progressBar = view.findViewById(R.id.progressBar);

        loginTextView = view.findViewById(R.id.textViewLoginHomePage);
        loginButton = view.findViewById(R.id.buttonLoginHomePage);
        forYouScrollView = view.findViewById(R.id.forYouScrollView);

        //GALLERY POPULAR
        galleryPopular = view.findViewById(R.id.homeGalleryPopular);

        //GALLERY BEST GAMES
        galleryBestGames = view.findViewById(R.id.homeGalleryBestGames);

        //GALLERY FOR YOU
        galleryForYou = view.findViewById(R.id.homeGalleryForYou);

        //GALLERY LATEST RELEASES
        galleryLatestReleases = view.findViewById(R.id.homeGalleryLatestReleases);

        //GALLERY INCOMING
        galleryIncoming = view.findViewById(R.id.homeGalleryIncoming);

        //LOGIN
        if(!isLogged()) {
            forYouScrollView.setVisibility(View.GONE);
            loginTextView.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            loginButton.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_loginActivity));
        }

        if (savedInstanceState != null) {
            if (isNetworkAvailable(requireContext())) {

                gamesPopular = savedInstanceState.getParcelableArrayList("popular");
                gamesBest = savedInstanceState.getParcelableArrayList("best");
                gamesLatestReleases = savedInstanceState.getParcelableArrayList("latest");
                gamesIncoming = savedInstanceState.getParcelableArrayList("incoming");

                showGames(0, gamesPopular);
                showGames(1, gamesLatestReleases);
                showGames(2, gamesIncoming);
                showGames(3, gamesBest);
                //showGames(4, gamesForYou); //NON SALVO I GIOCHI FOR YOU PERCHè ALMENO VENGONO SEMPRE AGGIORNATI SE L'UTENTE CAMBIA IL GENERE PREFERITO
            }else{
                Toast.makeText(requireContext(), R.string.no_connection_message, Toast.LENGTH_LONG).show();
            }
        }else if(gamesPopular!=null){ //SHARED PREFERENCE SALVATA

            gamesPopular = tinydb.getListObject("popular",GameApiResponse.class);
            gamesBest = tinydb.getListObject("best",GameApiResponse.class);
            gamesLatestReleases = tinydb.getListObject("latest",GameApiResponse.class);
            gamesIncoming = tinydb.getListObject("incoming",GameApiResponse.class);

            showGames(0, gamesPopular);
            showGames(1, gamesLatestReleases);
            showGames(2, gamesIncoming);
            showGames(3, gamesBest);
            //showGames(4, gamesForYou); //NON SALVO I GIOCHI FOR YOU PERCHè ALMENO VENGONO SEMPRE AGGIORNATI SE L'UTENTE CAMBIA IL GENERE PREFERITO
        }
        else { //SE SIA SHARED PREFERENCE E SIA ONSAVEINSTANCESTATE NON SONO STATI SALVATI ALLORA RI FA LE QUERY PER PRENDERE I DATI
            if (isNetworkAvailable(requireContext())) {
                progressBar.setVisibility(View.VISIBLE);

                gamesPopular = new ArrayList<>();
                gamesBest = new ArrayList<>();
                gamesLatestReleases = new ArrayList<>();
                gamesIncoming = new ArrayList<>();


                iGamesRepository.fetchGames(queryPopular, 10000, 0);
                iGamesRepository.fetchGames(queryLatestReleases, 10000, 1);
                iGamesRepository.fetchGames(queryIncoming, 10000, 2);
                iGamesRepository.fetchGames(queryBestGames, 10000, 3);

            } else {
                Toast.makeText(requireContext(), R.string.no_connection_message, Toast.LENGTH_LONG).show();
            }

        }
    }


    @Override
    public void onSuccess(List<GameApiResponse> gamesList, long lastUpdate,int countQuery) {
        if (countQuery == 0) {
            this.gamesPopular.addAll(gamesList);
            tinydb.putListObject("popular",gamesPopular);
            showGames(countQuery,gamesPopular);
        } else if (countQuery == 1) {
            this.gamesLatestReleases.addAll(gamesList);
            tinydb.putListObject("latest",gamesLatestReleases);
            showGames(countQuery,gamesLatestReleases);
        } else if (countQuery == 2) {
            this.gamesIncoming.addAll(gamesList);
            tinydb.putListObject("incoming",gamesIncoming);
            showGames(countQuery,gamesIncoming);
        } else if (countQuery == 3) {
            this.gamesBest.addAll(gamesList);
            tinydb.putListObject("best",gamesBest);
            showGames(countQuery,gamesBest);
        } else if(countQuery==4) {
            this.gamesForYou.addAll(gamesList);
            showGames(countQuery,gamesForYou);
        }else if(countQuery==5){
            List<String> genres=new ArrayList<>();
            List<GameApiResponse> gamesUser = new ArrayList<>();
            gamesUser.addAll(gamesList);
            for(int i = 0; i< gamesUser.size(); i++){
                game= gamesUser.get(i);
                if(game.getGenres()!=null) {
                    for (int j = 0; !game.getGenres().isEmpty() && j < game.getGenres().size(); j++) {
                        genres.add(game.getGenresString().get(j));
                    }
                }else{
                    if(gamesUser.size()==1) {
                        forYouScrollView.setVisibility(View.GONE);
                        loginButton.setVisibility(View.GONE);
                        loginTextView.setText(R.string.gameNoGenre); //setta textView con scritto che deve aggiungere almeno un gioco nella sezione "Giocato" per poter visualizzare i giochi consigliati
                        loginTextView.setVisibility(View.VISIBLE);
                        return;
                    }
                }
            }
            HashMap<String, Integer> mapGenreCount = new HashMap<>();

            for (int i = 0; i < genres.size(); i++) {
                if(genres.get(i)!=null) {
                    if (!mapGenreCount.containsKey(genres.get(i))) {
                        mapGenreCount.put(genres.get(i), 1);
                    } else {
                        int value = mapGenreCount.get(genres.get(i)) + 1;
                        mapGenreCount.put(genres.get(i), value);
                    }
                }
            }
            int maxValue = Integer.MIN_VALUE;
            for (Map.Entry<String, Integer> entry : mapGenreCount.entrySet()) {
                int value = entry.getValue();
                if (value > maxValue) {
                    maxValue = value;
                }
            }
            String preferredGenre = "";
            for (Map.Entry<String, Integer> entry : mapGenreCount.entrySet()) {
                if (entry.getValue().equals(maxValue)) {
                    preferredGenre = entry.getKey();
                    break;
                }
            }
            mapGenreCount.clear();

            queryForYou = "fields id,name,cover.url;where genres.name= \"" + preferredGenre + "\";limit 30;";
            if(isLogged()) {
                gamesForYou = new ArrayList<>();
                iGamesRepository.fetchGames(queryForYou, 10000, 4);
            }
        }

    }
    public void showGames(int countQuery, List<GameApiResponse> gameList){
        progressBar.setVisibility(View.GONE);
        TextView imageTextView;
        if(countQuery==0) {
            for (int i = 0; i < 30; i++) {
                game = gameList.get(i);
                Cover cover = game.getCover();
                ImageView imageGalleryPopular;
                if (cover != null) {
                    String uriString = cover.getUrl();
                    String newUri = uriString.replace("thumb", "cover_big_2x");
                    View viewItemHome = inflater.inflate(R.layout.itemhome, galleryPopular, false);
                    imageGalleryPopular = viewItemHome.findViewById(R.id.imageView);
                    Picasso.get().load(newUri).into(imageGalleryPopular);
                    galleryPopular.addView(viewItemHome);
                } else {
                    View viewItemHome = inflater.inflate(R.layout.itemhome, galleryPopular, false);
                    imageGalleryPopular = viewItemHome.findViewById(R.id.imageView);
                    imageTextView =viewItemHome.findViewById(R.id.imageTextView);
                    imageTextView.setVisibility(View.VISIBLE);
                    imageTextView.setText(game.getName());
                    imageGalleryPopular.setImageResource(R.drawable.background_grey);
                    galleryPopular.addView(viewItemHome);
                }
                int idGame=game.getId();
                imageGalleryPopular.setOnClickListener(v -> {
                    goToGameActivity(idGame, v);
                });
            }
        }else if(countQuery==1){
            for(int i=0;i<30;i++){
                game = gameList.get(i);
                Cover cover = game.getCover();
                ImageView imageGalleryLatestReleases;
                if(cover!=null) {
                    String uriString=cover.getUrl();
                    String newUri = uriString.replace("thumb", "cover_big_2x");
                    View viewItemHome = inflater.inflate(R.layout.itemhome, galleryLatestReleases, false);
                    imageGalleryLatestReleases = viewItemHome.findViewById(R.id.imageView);
                    Picasso.get().load(newUri).into(imageGalleryLatestReleases);
                    galleryLatestReleases.addView(viewItemHome);
                }
                else{
                    View viewItemHome = inflater.inflate(R.layout.itemhome, galleryLatestReleases, false);
                    imageGalleryLatestReleases = viewItemHome.findViewById(R.id.imageView);
                    imageTextView =viewItemHome.findViewById(R.id.imageTextView);
                    imageTextView.setVisibility(View.VISIBLE);
                    imageTextView.setText(game.getName());
                    imageGalleryLatestReleases.setImageResource(R.drawable.background_grey);
                    galleryLatestReleases.addView(viewItemHome);
                }
                int idGame=game.getId();
                imageGalleryLatestReleases.setOnClickListener(v -> {
                    goToGameActivity(idGame, v);
                });
            }
        }else if(countQuery==2){
            for(int i=0;i<30;i++){
                game = gameList.get(i);
                Cover cover = game.getCover();
                ImageView imageGalleryIncoming;
                if(cover!=null) {
                    String uriString=cover.getUrl();
                    String newUri = uriString.replace("thumb", "cover_big_2x");
                    View viewItemHome = inflater.inflate(R.layout.itemhome, galleryIncoming, false);
                    imageGalleryIncoming = viewItemHome.findViewById(R.id.imageView);
                    Picasso.get().load(newUri).into(imageGalleryIncoming);
                    galleryIncoming.addView(viewItemHome);
                }
                else{
                    View viewItemHome = inflater.inflate(R.layout.itemhome, galleryIncoming, false);
                    imageGalleryIncoming = viewItemHome.findViewById(R.id.imageView);
                    imageTextView =viewItemHome.findViewById(R.id.imageTextView);
                    imageTextView.setVisibility(View.VISIBLE);
                    imageTextView.setText(game.getName());
                    imageGalleryIncoming.setImageResource(R.drawable.background_grey);
                    galleryIncoming.addView(viewItemHome);
                }
                int idGame=game.getId();
                imageGalleryIncoming.setOnClickListener(v -> {
                    goToGameActivity(idGame, v);
                });
            }
        }
        else if(countQuery==3){
            for(int i=0;i<30;i++){
                game = gameList.get(i);
                Cover cover = game.getCover();
                ImageView imageGalleryBest;
                if(cover!=null) {
                    String uriString=cover.getUrl();
                    String newUri = uriString.replace("thumb", "cover_big_2x");
                    View viewItemHome = inflater.inflate(R.layout.itemhome, galleryBestGames, false);
                    imageGalleryBest = viewItemHome.findViewById(R.id.imageView);
                    Picasso.get().load(newUri).into(imageGalleryBest);
                    galleryBestGames.addView(viewItemHome);
                }
                else{
                    View viewItemHome = inflater.inflate(R.layout.itemhome, galleryBestGames, false);
                    imageGalleryBest = viewItemHome.findViewById(R.id.imageView);
                    imageTextView =viewItemHome.findViewById(R.id.imageTextView);
                    imageTextView.setVisibility(View.VISIBLE);
                    imageTextView.setText(game.getName());
                    imageGalleryBest.setImageResource(R.drawable.background_grey);
                    galleryBestGames.addView(viewItemHome);
                }
                int idGame=game.getId();
                imageGalleryBest.setOnClickListener(v -> {
                    goToGameActivity(idGame, v);
                });
            }
        }else if(countQuery==4) { //FOR YOU
                for (int i = 0; i < 30; i++) {
                    game = gameList.get(i);
                    Cover cover = game.getCover();
                    ImageView imageGalleryForYou;
                    if (cover != null) {
                        String uriString = cover.getUrl();
                        String newUri = uriString.replace("thumb", "cover_big_2x");
                        View viewItemHome = inflater.inflate(R.layout.itemhome, galleryForYou, false);
                        imageGalleryForYou = viewItemHome.findViewById(R.id.imageView);
                        Picasso.get().load(newUri).into(imageGalleryForYou);
                        galleryForYou.addView(viewItemHome);
                    } else {
                        View viewItemHome = inflater.inflate(R.layout.itemhome, galleryForYou, false);
                        imageGalleryForYou = viewItemHome.findViewById(R.id.imageView);
                        imageTextView =viewItemHome.findViewById(R.id.imageTextView);
                        imageTextView.setVisibility(View.VISIBLE);
                        imageTextView.setText(game.getName());
                        imageGalleryForYou.setImageResource(R.drawable.background_grey);
                        galleryForYou.addView(viewItemHome);
                    }
                    int idGame=game.getId();
                    imageGalleryForYou.setOnClickListener(v -> {
                        goToGameActivity(idGame, v);
                    });
                }
            }
        }

    private void goToGameActivity(int idGame, View v) {
        Bundle bundle = new Bundle();
        bundle.putInt("idGame", idGame);
        Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_gameActivity, bundle);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //tutti i giochi
        outState.putParcelableArrayList("popular", (ArrayList<? extends Parcelable>) gamesPopular);
        outState.putParcelableArrayList("best", (ArrayList<? extends Parcelable>) gamesBest);
        //outState.putParcelableArrayList("foryou", (ArrayList<? extends Parcelable>) gamesForYou);
        outState.putParcelableArrayList("latest", (ArrayList<? extends Parcelable>) gamesLatestReleases);
        outState.putParcelableArrayList("incoming", (ArrayList<? extends Parcelable>) gamesIncoming);

    }

    @Override
    public void onFailure(String errorMessage) {

    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private boolean isLogged() {
        firebaseAuth=FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("1091326442567-dbkvi0h9877eego2ou819bepnb05h65g.apps.googleusercontent.com").requestEmail().build();
        gsc = GoogleSignIn.getClient(requireContext(), gso);
        firebaseFirestore = FirebaseFirestore.getInstance();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());

        if (firebaseAuth.getCurrentUser() == null && account == null) {
            return false;
        } else {
            if (firebaseAuth.getCurrentUser() != null) {
                loggedUserID = firebaseAuth.getCurrentUser().getUid();
            } else if (account != null) {
                loggedUserID = account.getId();
            }
        }
        return true;
    }

    @Override
    public void onResume() { //QUANDO SI PASSA DALLA GAMEACTIVITY (DOVE SI AGGIUNGE UN GIOCO IN GIOCATO CHE CAMBIA IL GENERE) AL FRAGMENT HOME QUESTO MOSTRERà SUBITO I GIOCHI PER TE
        super.onResume();
        if(isLogged()) {
            DocumentReference docRef = firebaseFirestore.collection("User").document(loggedUserID);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        ArrayList<Integer> playedGames = (ArrayList<Integer>) document.get("playedGames");
                        Integer gameId;
                        if (!playedGames.isEmpty()) {

                            loginTextView.setVisibility(View.GONE);
                            loginButton.setVisibility(View.GONE);
                            forYouScrollView.setVisibility(View.VISIBLE);

                            String games = "";
                            for (int i = 0; i < playedGames.size(); i++) {
                                if (i < playedGames.size() - 1) {
                                    gameId = Integer.parseInt(String.valueOf(playedGames.get(i)));
                                    games = games + gameId + ",";
                                } else {
                                    gameId = Integer.parseInt(String.valueOf(playedGames.get(i)));
                                    games = games + gameId;
                                }
                            }
                            String queryGenres = "fields genres.name;where id=(" + games + ");";
                            iGamesRepository.fetchGames(queryGenres, 10000, 5); //query per recuperare generi dei giochi giocati dall'utente
                        } else {
                            forYouScrollView.setVisibility(View.GONE);
                            loginButton.setVisibility(View.GONE);
                            loginTextView.setText(R.string.loginPlayedGame); //setta textView con scritto che deve aggiungere almeno un gioco nella sezione "Giocato" per poter visualizzare i giochi consigliati
                            loginTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
    }
}