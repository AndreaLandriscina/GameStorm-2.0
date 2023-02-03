package com.example.gamestorm.ui;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements ResponseCallback {
    private LinearLayout galleryPopular;
    private LinearLayout galleryBestGames;
    private LinearLayout galleryForYou;
    private LinearLayout galleryLatestReleases;
    private LinearLayout galleryIncoming;
    private ImageView imageGalleryPopular;
    private ImageView imageGalleryBest;
    private ImageView imageGalleryForYou;
    private ImageView imageGalleryLatestReleases;
    private ImageView imageGalleryIncoming;

    private TextView loginTextView;
    private Button loginButton;
    private HorizontalScrollView forYouScrollView;

    private TextView imageTextView;
    private ProgressBar progressBar;

    LayoutInflater inflater;

    int idGame = 0;
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

    String multiQuery;

    //LOGIN
    FirebaseAuth firebaseAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FirebaseFirestore firebaseFirestore;
    String loggedUserID;

    //SHAREDPREFERENCE
    TinyDB tinydb;

    private List<GameApiResponse> gamesUser;
    private List<String> genres;


    public HomeFragment() {

    }

    private long currentDate() {
        Calendar calendar = Calendar.getInstance();
        long timeSeconds = calendar.getTimeInMillis()/1000;
        return timeSeconds;
    }



    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tinydb = new TinyDB(getActivity().getApplication());

        iGamesRepository = new GamesRepository(getActivity().getApplication(), this);
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
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
            });
        }else{
            DocumentReference docRef = firebaseFirestore.collection("User").document(loggedUserID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            ArrayList<Integer> playedGames = (ArrayList<Integer>) document.get("playedGames");
                            Integer gameId=0;
                            if (!playedGames.isEmpty()) {

                                loginTextView.setVisibility(View.GONE);
                                loginButton.setVisibility(View.GONE);
                                forYouScrollView.setVisibility(View.VISIBLE);

                                String games = "";
                                for (int i = 0; i < playedGames.size(); i++) {
                                    if (i < playedGames.size() - 1) {
                                        gameId = Integer.parseInt(String.valueOf(playedGames.get(i)));
                                        games=games+gameId+",";
                                    } else {
                                        gameId = Integer.parseInt(String.valueOf(playedGames.get(i)));
                                        games=games+gameId;
                                    }
                                }
                                String queryGenres = "fields genres.name;where id=(" + games + ");";
                                iGamesRepository.fetchGames(queryGenres, 10000, 5); //query per recuperare generi dei giochi giocati dall'utente
                            }else{
                                forYouScrollView.setVisibility(View.GONE);
                                loginButton.setVisibility(View.GONE);
                                loginTextView.setText(R.string.loginPlayedGame); //setta textView con scritto che deve aggiungere almeno un gioco nella sezione "Giocato" per poter visualizzare i giochi consigliati
                                loginTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            });
        }

        if (savedInstanceState != null) {
            if (isNetworkAvailable(getContext())) {

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
                Snackbar.make(view.findViewById(R.id.Coordinatorlyt), R.string.no_connection_message, Snackbar.LENGTH_LONG).show();
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
            if (isNetworkAvailable(getContext())) {
                progressBar.setVisibility(View.VISIBLE);

                gamesPopular = new ArrayList<>();
                gamesBest = new ArrayList<>();
                gamesLatestReleases = new ArrayList<>();
                gamesIncoming = new ArrayList<>();
                gamesUser=new ArrayList<>();

                iGamesRepository.fetchGames(queryPopular, 10000, 0);
                iGamesRepository.fetchGames(queryLatestReleases, 10000, 1);
                iGamesRepository.fetchGames(queryIncoming, 10000, 2);
                iGamesRepository.fetchGames(queryBestGames, 10000, 3);

            } else {
                Snackbar.make(view.findViewById(R.id.Coordinatorlyt), R.string.no_connection_message, Snackbar.LENGTH_LONG).show();
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
            tinydb.putListObject("forYou",gamesForYou);
            showGames(countQuery,gamesForYou);
        }else if(countQuery==5){
            genres=new ArrayList<>();
            this.gamesUser.addAll(gamesList);
            for(int i=0;i<gamesUser.size();i++){
                game=gamesUser.get(i);
                for(int j=0;j<game.getGenres().size() && game.getGenres()!=null;j++) {
                    genres.add(game.getGenresString().get(j).toString());
                }
            }
            HashMap<String, Integer> mapGenreCount = new HashMap<>();
            for (int i = 0; i < genres.size(); i++) {
                if (!mapGenreCount.containsKey(genres.get(i))) {
                    mapGenreCount.put(genres.get(i), 1);
                } else {
                    int value = mapGenreCount.get(genres.get(i)) + 1;
                    mapGenreCount.put(genres.get(i), value);
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
            queryForYou = "fields id,name,cover.url;where genres.name= \"" + preferredGenre + "\";limit 30;";
            if(isLogged() && queryForYou!=null) {
                gamesForYou = new ArrayList<>();
                iGamesRepository.fetchGames(queryForYou, 10000, 4);
            }
        }

    }
    public void showGames(int countQuery, List<GameApiResponse> gameList){
        progressBar.setVisibility(View.GONE);
        if(countQuery==0) {
            for (int i = 0; i < 30; i++) {
                game = gameList.get(i);
                Cover cover = game.getCover();
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
                    imageTextView=viewItemHome.findViewById(R.id.imageTextView);
                    imageTextView.setVisibility(View.VISIBLE);
                    imageTextView.setText(game.getName());
                    imageGalleryPopular.setImageResource(R.drawable.background_grey);
                    galleryPopular.addView(viewItemHome);
                }
                int idGame=game.getId();
                imageGalleryPopular.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), GameActivity.class);
                        i.putExtra("idGame", idGame);
                        startActivity(i);
                    }
                });
            }
        }else if(countQuery==1){
            for(int i=0;i<30;i++){
                game = gameList.get(i);
                Cover cover = game.getCover();
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
                    imageTextView=viewItemHome.findViewById(R.id.imageTextView);
                    imageTextView.setVisibility(View.VISIBLE);
                    imageTextView.setText(game.getName());
                    imageGalleryLatestReleases.setImageResource(R.drawable.background_grey);
                    galleryLatestReleases.addView(viewItemHome);
                }
                int idGame=game.getId();
                imageGalleryLatestReleases.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), GameActivity.class);
                        i.putExtra("idGame", idGame);
                        startActivity(i);
                    }
                });
            }
        }else if(countQuery==2){
            for(int i=0;i<30;i++){
                game = gameList.get(i);
                Cover cover = game.getCover();
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
                    imageTextView=viewItemHome.findViewById(R.id.imageTextView);
                    imageTextView.setVisibility(View.VISIBLE);
                    imageTextView.setText(game.getName());
                    imageGalleryIncoming.setImageResource(R.drawable.background_grey);
                    galleryIncoming.addView(viewItemHome);
                }
                int idGame=game.getId();
                imageGalleryIncoming.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), GameActivity.class);
                        i.putExtra("idGame", idGame);
                        startActivity(i);
                    }
                });
            }
        }
        else if(countQuery==3){
            for(int i=0;i<30;i++){
                game = gameList.get(i);
                Cover cover = game.getCover();
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
                    imageTextView=viewItemHome.findViewById(R.id.imageTextView);
                    imageTextView.setVisibility(View.VISIBLE);
                    imageTextView.setText(game.getName());
                    imageGalleryBest.setImageResource(R.drawable.background_grey);
                    galleryBestGames.addView(viewItemHome);
                }
                int idGame=game.getId();
                imageGalleryBest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), GameActivity.class);
                        i.putExtra("idGame", idGame);
                        startActivity(i);
                    }
                });
            }
        }else if(countQuery==4) { //FOR YOU
                for (int i = 0; i < 30; i++) {
                    game = gameList.get(i);
                    Cover cover = game.getCover();
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
                        imageTextView=viewItemHome.findViewById(R.id.imageTextView);
                        imageTextView.setVisibility(View.VISIBLE);
                        imageTextView.setText(game.getName());
                        imageGalleryForYou.setImageResource(R.drawable.background_grey);
                        galleryForYou.addView(viewItemHome);
                    }
                    int idGame=game.getId();
                    imageGalleryForYou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getContext(), GameActivity.class);
                            i.putExtra("idGame",idGame);
                            startActivity(i);
                        }
                    });
                }
            }
        }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //tutti i giochi
        outState.putParcelableArrayList("popular", (ArrayList<? extends Parcelable>) gamesPopular);
        outState.putParcelableArrayList("best", (ArrayList<? extends Parcelable>) gamesBest);
        outState.putParcelableArrayList("foryou", (ArrayList<? extends Parcelable>) gamesForYou);
        outState.putParcelableArrayList("latest", (ArrayList<? extends Parcelable>) gamesLatestReleases);
        outState.putParcelableArrayList("incoming", (ArrayList<? extends Parcelable>) gamesIncoming);

    }

    @Override
    public void onFailure(String errorMessage) {

    }

    @Override
    public void onGameFavoriteStatusChanged(GameApiResponse game) {

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
        gsc = GoogleSignIn.getClient(getContext(), gso);
        firebaseFirestore = FirebaseFirestore.getInstance();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

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
}