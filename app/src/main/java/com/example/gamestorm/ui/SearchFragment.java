package com.example.gamestorm.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gamestorm.Model.GameApiResponse;
import com.example.gamestorm.Model.Genre;
import com.example.gamestorm.Model.Platform;
import com.example.gamestorm.R;
import com.example.gamestorm.Repository.GamesRepository;
import com.example.gamestorm.Repository.IGamesRepository;
import com.example.gamestorm.util.ResponseCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class SearchFragment extends Fragment implements ResponseCallback {

    private IGamesRepository iGamesRepository;
    private List<GameApiResponse> games;
    private EditText gameName;
    private Button search;
    private Button sorting;
    private Button filters;
    private TextView numberOfResults;
    private RecyclerView gamesRV;
    GameAdapter adapter;
    private String sortingParameter;
    private int lastSelectedSortingParameter;
    private int lastSelectedGenre;
    private int lastSelectedPlatform;
    private int lastSelectedReleaseYear;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iGamesRepository = new GamesRepository(getActivity().getApplication(), this);
        games = new ArrayList<>();
        gameName = view.findViewById(R.id.game_name_ET);
        search = view.findViewById(R.id.search_B);
        sorting = view.findViewById(R.id.sorting_B);
        filters = view.findViewById(R.id.filters_B);
        numberOfResults = view.findViewById(R.id.number_of_results_TV);
        gamesRV = view.findViewById(R.id.games_RV);
        sortingParameter = "";
        lastSelectedSortingParameter = -1;
        lastSelectedGenre = 0;
        lastSelectedPlatform = 0;
        lastSelectedReleaseYear = 0;

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_input = gameName.getText().toString();
                //timestamp per ottenere solo giochi già usciti(su igdb si sono giochi che devono ancora uscire e che non hanno informazioni utili per l'utente)
                String query = "fields id, name, cover.url, follows, rating, first_release_date, genres.name, platforms.name; where first_release_date < "+System. currentTimeMillis()/1000+" & version_parent = null;search \""+user_input+"\"; limit 500;";
                iGamesRepository.fetchGames(query,10000);
            }
        });

        sorting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SOSTITUIRE ALERTDIALOG CON DIALOG

                // AlertDialog builder instance to build the alert dialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

                // title of the alert dialog
                alertDialog.setTitle("Sort by");

                // list of the items to be displayed to the user in the
                // form of list so that user can select the item from
                final String[] listItems = new String[]{"Most popular", "Most recent", "Best rating", "Alphabet"};  //DA ESTRARRE IN RESOURCES

                // the function setSingleChoiceItems is the function which
                // builds the alert dialog with the single item selection
                alertDialog.setSingleChoiceItems(listItems, lastSelectedSortingParameter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sortingParameter = listItems[i];
                        lastSelectedSortingParameter = i;

                        //sorting decrescente
                        Collections.sort(games, (o1, o2) -> {
                            //non bello
                            int result = 0;
                            switch (sortingParameter){
                                case "Most popular":
                                    result = - Integer.compare(o1.getFollows(), o2.getFollows());
                                    break;

                                case "Most recent":
                                    try{
                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                        Date date1 = formatter.parse(o1.getFirstReleaseDate());
                                        Date date2 = formatter.parse(o2.getFirstReleaseDate());
                                        result = - date1.compareTo(date2);
                                    }catch (ParseException e1){
                                        e1.printStackTrace();
                                    }
                                    break;

                                case "Best rating":
                                    result = - Double.compare(o1.getRating(), o2.getRating());
                                    break;

                                case "Alphabet":
                                    result = o1.getName().compareTo(o2.getName());
                                    break;

                            }
                            return result;
                        });
                        adapter.notifyDataSetChanged();
                        dialogInterface.dismiss();
                    }
                });

                // set the negative button if the user is not interested to select or change already selected item
                alertDialog.setNegativeButton("Cancel", (dialog, which) -> {

                });

                // create and build the AlertDialog instance with the AlertDialog builder instance
                AlertDialog customAlertDialog = alertDialog.create();

                // show the alert dialog when the button is clicked
                customAlertDialog.show();
            }
        });

        //DA FARE
        filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog filtersDialog = new Dialog(getContext());
                filtersDialog.setContentView(R.layout.dialog_filters);

                Spinner genreSPN = filtersDialog.findViewById(R.id.genre_SPN);
                Spinner platformSPN = filtersDialog.findViewById(R.id.platform_SPN);
                Spinner releaseYearSPN = filtersDialog.findViewById(R.id.releaseyear_SPN);
                Button cancelB = filtersDialog.findViewById(R.id.cancel_B);
                Button confirmB = filtersDialog.findViewById(R.id.confirm_B);


                String[] genres = {"Any genre","Fighting","Shooter","Music","Platform","Puzzle","Racing","Real Time Strategy (RTS)","Role-playing (RPG)"
                        ,"Simulator","Sport","Strategy","Turn-based strategy (TBS)","Tactical","Quiz/Trivia",
                        "Hack and slash/Beat 'em up","Pinball","Adventure","Arcade","Visual Novel","Indie",
                        "Card & Board Game","MOBA","Point-and-click"};

                String[] platforms = {"Any platform","Platform1","Platform2","Platform3"};

                List<String> years = new ArrayList<>();
                years.add("Any year");
                for (int i = Calendar.getInstance().get(Calendar.YEAR); i >= 1958; i--){
                    years.add(""+i);
                }

                ArrayAdapter<String> genreAdapter = new ArrayAdapter<String>(getContext(),  android.R.layout.simple_spinner_dropdown_item, genres);
                genreAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
                genreSPN.setAdapter(genreAdapter);
                genreSPN.setSelection(lastSelectedGenre);

                ArrayAdapter<String> platformAdapter = new ArrayAdapter<String>(getContext(),  android.R.layout.simple_spinner_dropdown_item, platforms);
                platformAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
                platformSPN.setAdapter(platformAdapter);
                platformSPN.setSelection(lastSelectedPlatform);

                ArrayAdapter<String> releaseYearAdapter = new ArrayAdapter<String>(getContext(),  android.R.layout.simple_spinner_dropdown_item, years);
                releaseYearAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
                releaseYearSPN.setAdapter(releaseYearAdapter);
                releaseYearSPN.setSelection(lastSelectedReleaseYear);

                cancelB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filtersDialog.dismiss();
                    }
                });

                confirmB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String genreInput, platformInput, releaseyearInput;

                        genreInput = genreSPN.getSelectedItem().toString();
                        platformInput = platformSPN.getSelectedItem().toString();
                        releaseyearInput = releaseYearSPN.getSelectedItem().toString();

                        lastSelectedGenre = genreAdapter.getPosition(genreInput);
                        lastSelectedPlatform = platformAdapter.getPosition(platformInput);
                        lastSelectedReleaseYear = releaseYearAdapter.getPosition(releaseyearInput);

                        filtersDialog.dismiss();

                        List<GameApiResponse> gamesToShow = new ArrayList<>();


                        if(genreInput.equals("Any genre") && platformInput.equals("Any platform") && releaseyearInput.equals("Any year")){
                            gamesToShow = games;
                        }else{

                            if(!genreInput.equals("Any genre")){
                                for (GameApiResponse game : games){
                                    boolean hasGenre = false;

                                    List<Genre> gameGenres = game.getGenres();

                                    if(gameGenres != null){
                                        for (Genre genre : gameGenres){
                                            if(genre.getName().equals(genreInput)){
                                                hasGenre = true;
                                            }
                                        }

                                        if(hasGenre){
                                            gamesToShow.add(game);
                                        }
                                    }
                                }
                            }

                            if(!platformInput.equals("Any platform")){
                                for (GameApiResponse game : games){
                                    boolean hasPlatform = false;

                                    List<Platform> gamePlatforms = game.getPlatforms();

                                    if(gamePlatforms != null) {
                                        for (Platform platform : gamePlatforms) {
                                            if (platform.getName().equals(platformInput)) {
                                                hasPlatform = true;
                                            }
                                        }

                                        if (hasPlatform) {
                                            gamesToShow.add(game);
                                        }
                                    }
                                }
                            }

                        }

                        if(!releaseyearInput.equals("Any year")){
                            for (GameApiResponse game : games){
                                String [] dateParts = game.getFirstReleaseDate().split("/");
                                String yearOfRelease = dateParts[2];
                                if(yearOfRelease.equals(releaseyearInput))
                                    gamesToShow.add(game);
                            }
                        }


                        numberOfResults.setText(gamesToShow.size() + " results found for " + "\""+ gameName.getText() + "\"");
                        showGamesOnRecyclerView(gamesToShow);
                        adapter.notifyDataSetChanged();

                    }
                });

                filtersDialog.show();
            }
        });
    }

    @Override
    public void onSuccess(List<GameApiResponse> gamesList, long lastUpdate) {
        games = gamesList;
        numberOfResults.setText(gamesList.size() + " results found for " + "\""+ gameName.getText() + "\"");
        showGamesOnRecyclerView(games);
    }

    @Override
    public void onFailure(String errorMessage) {
        Log.e("TAG", "query errata");
    }

    @Override
    public void onGameFavoriteStatusChanged(GameApiResponse game) {

    }

    public void showGamesOnRecyclerView(List<GameApiResponse> gamesList){
        // added data from arraylist to adapter class.
        adapter=new GameAdapter(gamesList,getContext());

        // setting grid layout manager to implement grid view.
        // in this method '2' represents number of columns to be displayed in grid view.
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),3);

        // at last set adapter to recycler view.
        gamesRV.setLayoutManager(layoutManager);
        gamesRV.setAdapter(adapter);
    }
}