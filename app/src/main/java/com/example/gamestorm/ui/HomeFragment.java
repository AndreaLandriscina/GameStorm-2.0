package com.example.gamestorm.ui;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gamestorm.model.Cover;
import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.GamesRepository;
import com.example.gamestorm.repository.IGamesRepository;
import com.example.gamestorm.util.ResponseCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


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

        iGamesRepository = new GamesRepository(getActivity().getApplication(), this);
        queryPopular = "fields id, name, cover.url; sort follows desc; limit 30;";
        queryLatestReleases = "fields id, name, cover.url; sort first_release_date desc; limit 30;";
        queryIncoming = "fields id, name, cover.url; where first_release_date > " +Long.toString(currentDate())+";sort first_release_date asc; limit 30;";
        queryBestGames="fields id, name, cover.url;where total_rating_count>1000;sort total_rating desc;limit 30;";
        queryForYou="fields id, name, cover.url; sort follows desc; limit 30;";

        //multiQuery="query games \"Popular\" {" + queryPopular + "};" +"query games \"Latest\" {" + queryLatestReleases  + "};" +"query games \"Incoming\" {" + queryIncoming + "};";
        multiQuery="query games \"Popular\" {fields id, cover.url; sort rating desc; limit 30;};  " +
                "query games \"Latest\" {fields id, cover.url; sort date desc; limit 30;}; " +
                "query games \"Incoming\" {fields id, cover.url; sort date asc; limit 30;};";
        gamesPopular = new ArrayList<>();
        gamesBest=new ArrayList<>();
        gamesForYou=new ArrayList<>();
        gamesLatestReleases=new ArrayList<>();
        gamesIncoming=new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflater=LayoutInflater.from(getContext());

        loginTextView=view.findViewById(R.id.textViewLoginHomePage);
        loginButton=view.findViewById(R.id.buttonLoginHomePage);
        forYouScrollView=view.findViewById(R.id.forYouScrollView);

        //GALLERY POPULAR
        galleryPopular=view.findViewById(R.id.homeGalleryPopular);

        //GALLERY BEST GAMES
        galleryBestGames=view.findViewById(R.id.homeGalleryBestGames);

        //GALLERY FOR YOU
        galleryForYou=view.findViewById(R.id.homeGalleryForYou);

        //GALLERY LATEST RELEASES
        galleryLatestReleases=view.findViewById(R.id.homeGalleryLatestReleases);

        //GALLERY INCOMING
        galleryIncoming=view.findViewById(R.id.homeGalleryIncoming);

        iGamesRepository.fetchGames(queryPopular,10000,0);
        iGamesRepository.fetchGames(queryLatestReleases,10000,1);
        iGamesRepository.fetchGames(queryIncoming,10000,2);
        iGamesRepository.fetchGames(queryBestGames,10000,3);
        iGamesRepository.fetchGames(queryForYou,10000,4);

    }


    @Override
    public void onSuccess(List<GameApiResponse> gamesList, long lastUpdate,int countQuery) {
        if(countQuery==0){
            this.gamesPopular.addAll(gamesList);
        }
        else if(countQuery==1){
            this.gamesLatestReleases.addAll(gamesList);
        }
        else if(countQuery==2){
            this.gamesIncoming.addAll(gamesList);
        }else if(countQuery==3){
            this.gamesBest.addAll(gamesList);
        }else{
            this.gamesForYou.addAll(gamesList);
        }

        if(countQuery==0) {
            for (int i = 0; i < 30; i++) {
                game = gamesPopular.get(i);
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
                imageGalleryPopular.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      /* Bundle bundle=new Bundle();
                       bundle.putInt("id",game.getId());
                       Navigation.findNavController(getView()).navigate(R.id.fromHometoGame);
                       */
                        Intent i = new Intent(getContext(), GameActivity.class);
                        i.putExtra("idGame", game.getId());
                        startActivity(i);
                    }
                });
            }
        }else if(countQuery==1){
            for(int i=0;i<30;i++){
                game = gamesLatestReleases.get(i);
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
                imageGalleryLatestReleases.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       /*Bundle bundle=new Bundle();
                       bundle.putInt("id",game.getId());
                       Navigation.findNavController(getView()).navigate(R.id.fromHometoGame);*/
                        Intent i = new Intent(getContext(), GameActivity.class);
                        i.putExtra("idGame", game.getId());
                        startActivity(i);
                    }
                });
            }
        }else if(countQuery==2){
            for(int i=0;i<30;i++){
                game = gamesIncoming.get(i);
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
                imageGalleryIncoming.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       /*Bundle bundle=new Bundle();
                       bundle.putInt("id",game.getId());
                       Navigation.findNavController(getView()).navigate(R.id.fromHometoGame);*/
                        Intent i = new Intent(getContext(), GameActivity.class);
                        i.putExtra("idGame", game.getId());
                        startActivity(i);
                    }
                });
            }
        }
        else if(countQuery==3){
            for(int i=0;i<30;i++){
                game = gamesBest.get(i);
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
                imageGalleryBest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       /*Bundle bundle=new Bundle();
                       bundle.putInt("id",game.getId());
                       Navigation.findNavController(getView()).navigate(R.id.fromHometoGame);*/
                        Intent i = new Intent(getContext(), GameActivity.class);
                        i.putExtra("idGame", game.getId());
                        startActivity(i);
                    }
                });
            }
        }else { //FOR YOU
            boolean logged = false;
            if (logged) {
                loginTextView.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                forYouScrollView.setVisibility(View.VISIBLE);
                for (int i = 0; i < 30; i++) {
                    game = gamesForYou.get(i);
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
                    imageGalleryForYou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           /*Bundle bundle = new Bundle();
                           bundle.putInt("id", game.getId());
                           Navigation.findNavController(getView()).navigate(R.id.fromHometoGame);*/
                            Intent i = new Intent(getContext(), GameActivity.class);
                            i.putExtra("idGame", game.getId());
                            startActivity(i);
                        }
                    });
                }
            }else{
                forYouScrollView.setVisibility(View.GONE);
                loginTextView.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.VISIBLE);
            }
        }

    }


    @Override
    public void onFailure(String errorMessage) {

    }

    @Override
    public void onGameFavoriteStatusChanged(GameApiResponse game) {

    }
}