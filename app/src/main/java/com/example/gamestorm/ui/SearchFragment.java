package com.example.gamestorm.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gamestorm.adapter.GameAdapter;
import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.model.Genre;
import com.example.gamestorm.model.Platform;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.GamesRepository;
import com.example.gamestorm.repository.IGamesRepository;
import com.example.gamestorm.util.ResponseCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class SearchFragment extends Fragment implements ResponseCallback {

    private IGamesRepository iGamesRepository;
    private boolean firstLoad;
    private List<GameApiResponse> games;
    private List<GameApiResponse> gamesCopy;
    private SearchView gameName;
    private String userInput;
    private MaterialButton sorting;
    private MaterialButton filters;
    private TextView numberOfResults;
    private RecyclerView gamesRV;
    private ProgressBar searchLoading;
    GameAdapter adapter;
    private String sortingParameter;
    private int lastSelectedSortingParameter;
    private int lastSelectedGenre;
    private int lastSelectedPlatform;
    private int lastSelectedReleaseYear;

    //savedInstanceState keys
    private final String gameNameKey = "GAME_NAME";
    private final String sortingParameterKey = "SORTING_PARAMETER";
    private final String lastSelectedSortingParameterKey = "LAST_SORTING_PARAMETER";
    private final String lastSelectedGenreKey = "GENRE";
    private final String lastSelectedPlatformKey = "PLATFORM";
    private final String lastSelectedReleaseYearKey = "RELEASE_YEAR";
    private final String gamesKey = "GAMES";
    private final String gamesCopyKey = "GAMES_COPY";
    private final String firstLoadKey = "FIRST_LOAD";
    private final String resultNumberKey = "RESULTS_NUMBER";



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

        if(getActivity() != null)
            iGamesRepository = new GamesRepository(getActivity().getApplication(), this);
        firstLoad = true;
        games = new ArrayList<>();
        gameName = view.findViewById(R.id.game_name_SV);
        userInput = "";
        sorting = view.findViewById(R.id.sorting_B);
        filters = view.findViewById(R.id.filters_B);
        numberOfResults = view.findViewById(R.id.number_of_results_TV);
        gamesRV = view.findViewById(R.id.games_RV);
        searchLoading = view.findViewById(R.id.search_loading_PB);
        sortingParameter = "";
        lastSelectedSortingParameter = -1;
        lastSelectedGenre = 0;
        lastSelectedPlatform = 0;
        lastSelectedReleaseYear = 0;

        if(savedInstanceState != null){
            userInput = savedInstanceState.getString(gameNameKey);
            sortingParameter = savedInstanceState.getString(sortingParameterKey);
            lastSelectedSortingParameter = savedInstanceState.getInt(lastSelectedSortingParameterKey);
            lastSelectedGenre = savedInstanceState.getInt(lastSelectedGenreKey);
            lastSelectedPlatform = savedInstanceState.getInt(lastSelectedPlatformKey);
            lastSelectedReleaseYear = savedInstanceState.getInt(lastSelectedReleaseYearKey);
            games = savedInstanceState.getParcelableArrayList(gamesKey);
            gamesCopy = savedInstanceState.getParcelableArrayList(gamesCopyKey);
            firstLoad = savedInstanceState.getBoolean(firstLoadKey);

            if(firstLoad){
                if(getContext()!= null && isNetworkAvailable(getContext())){
                    numberOfResults.setTextSize(30);
                    numberOfResults.setTypeface(null, Typeface.BOLD);
                    numberOfResults.setText(R.string.explore_title);
                    searchLoading.setVisibility(View.VISIBLE);
                    String queryToServer = "fields id, name, cover.url, follows, rating, first_release_date, genres.name, platforms.name; where cover.url != null; limit 500;";
                    iGamesRepository.fetchGames(queryToServer, 10000,0);
                }else{
                    Snackbar.make(view.findViewById(R.id.Coordinatorlyt), R.string.no_connection_message, Snackbar.LENGTH_LONG).show();
                }
            }else {
                numberOfResults.setTextSize(15);
                numberOfResults.setTypeface(null, Typeface.NORMAL);
                numberOfResults.setText(savedInstanceState.getString(resultNumberKey));
            }



            if(!games.isEmpty()){
                showGamesOnRecyclerView(games);
                adapter.notifyDataSetChanged();
                sorting.setVisibility(View.VISIBLE);
                filters.setVisibility(View.VISIBLE);
            }

        }else{
            if(getContext()!= null && isNetworkAvailable(getContext())){
                //mostro i piu popolari la prima volta che si accede al fragment
                if(firstLoad){
                    numberOfResults.setText(R.string.explore_title);
                    numberOfResults.setTextSize(30);
                    numberOfResults.setTypeface(null, Typeface.BOLD);
                }
                searchLoading.setVisibility(View.VISIBLE);
                String queryToServer = "fields id, name, cover.url, follows, rating, first_release_date, genres.name, platforms.name; where cover.url != null; limit 500;";
                iGamesRepository.fetchGames(queryToServer, 10000,0);
            }else{

                Snackbar.make(view.findViewById(R.id.Coordinatorlyt), R.string.no_connection_message, Snackbar.LENGTH_LONG).show();
            }

        }



        gameName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firstLoad = false;

                if(!games.isEmpty()){
                    games.clear();
                    gamesCopy.clear();
                    adapter.notifyDataSetChanged();
                }

                lastSelectedSortingParameter = -1;
                lastSelectedGenre = 0;
                lastSelectedPlatform = 0;
                lastSelectedReleaseYear = 0;
                numberOfResults.setText("");

                if(getContext()!= null && isNetworkAvailable(getContext())){

                    userInput = query;
                    //timestamp per ottenere solo giochi già usciti(su igdb si sono giochi che devono ancora uscire e che non hanno informazioni utili per l'utente)
                    String queryToServer = "fields id, name, cover.url, follows, rating, first_release_date, genres.name, platforms.name; where first_release_date < " + System.currentTimeMillis() / 1000 + " & version_parent = null;search \"" + userInput + "\"; limit 500;";
                    searchLoading.setVisibility(View.VISIBLE);
                    numberOfResults.setText("");
                    iGamesRepository.fetchGames(queryToServer, 10000,0);
                }else{
                    Snackbar.make(view.findViewById(R.id.Coordinatorlyt), R.string.no_connection_message, Snackbar.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



        sorting.setOnClickListener(v -> {

            final String[] listItems = getContext().getResources().getStringArray(R.array.sorting_parameters);

            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(R.string.sort_by_dialog_title)
                    .setSingleChoiceItems(listItems, lastSelectedSortingParameter, (dialog, i) -> {
                        sortingParameter = listItems[i];
                        lastSelectedSortingParameter = i;

                        if(isNetworkAvailable(getContext()) || !games.isEmpty()){
                            //sorting decrescente
                            Collections.sort(games, (o1, o2) -> {
                                //non bello
                                int result = 0;
                                switch (sortingParameter) {
                                    case "Most popular":
                                    case "Più popolare":
                                        result = -Integer.compare(o1.getFollows(), o2.getFollows());
                                        break;
                                    case "Most recent":
                                    case "Più recente":
                                        try {
                                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                            Date date1 = formatter.parse(o1.getFirstReleaseDate());
                                            Date date2 = formatter.parse(o2.getFirstReleaseDate());
                                            result = -date1.compareTo(date2);
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }
                                        break;
                                    case "Best rating":
                                    case "Voto migliore":
                                        result = -Double.compare(o1.getRating(), o2.getRating());
                                        break;

                                    case "Alphabet":
                                    case "Alfabeto":
                                        result = o1.getName().compareTo(o2.getName());
                                        break;

                                }
                                return result;
                            });
                            adapter.notifyDataSetChanged();
                            showGamesOnRecyclerView(games);

                        }else{

                            Snackbar.make(view.findViewById(R.id.Coordinatorlyt), R.string.no_connection_message, Snackbar.LENGTH_LONG).show();
                        }

                        dialog.dismiss();

                    })
                    .setNegativeButton(R.string.cancel_text, (dialogInterface, i) -> {

                    })
                    .show();


        });


        filters.setOnClickListener(v -> {

            final View customLayout = getLayoutInflater().inflate(R.layout.dialog_filters, null);

            Spinner genreSPN = customLayout.findViewById(R.id.genre_SPN);
            Spinner platformSPN = customLayout.findViewById(R.id.platform_SPN);
            Spinner releaseYearSPN = customLayout.findViewById(R.id.releaseyear_SPN);



            String[] genres = {"Any genre", "Fighting", "Shooter", "Music", "Platform", "Puzzle", "Racing", "Real Time Strategy (RTS)", "Role-playing (RPG)"
                    , "Simulator", "Sport", "Strategy", "Turn-based strategy (TBS)", "Tactical", "Quiz/Trivia",
                    "Hack and slash/Beat 'em up", "Pinball", "Adventure", "Arcade", "Visual Novel", "Indie",
                    "Card & Board Game", "MOBA", "Point-and-click"};
            genres[0] = getContext().getResources().getString(R.string.any_genre);

            String[] platforms = {"Any platform", "Commodore CDTV",
                    "Sega Pico",
                    "PlayStation 2",
                    "iOS",
                    "Commodore Plus/4",
                    "AY-3-8710",
                    "Odyssey",
                    "Commodore PET",
                    "Sol-20",
                    "PC (Microsoft Windows)",
                    "Tapwave Zodiac",
                    "ColecoVision",
                    "PlayStation VR",
                    "Texas Instruments TI-99",
                    "Acorn Electron",
                    "Gamate",
                    "Hyper Neo Geo 64",
                    "Thomson MO5",
                    "Odyssey 2 / Videopac G7000",
                    "SteamVR",
                    "PC-50X Family",
                    "AY-3-8607",
                    "AY-3-8605",
                    "AY-3-8606",
                    "PC-98",
                    "Amstrad CPC",
                    "Playdate",
                    "Family Computer Disk System",
                    "WonderSwan Color",
                    "Neo Geo CD",
                    "Sega Game Gear",
                    "Atari Jaguar",
                    "3DO Interactive Multiplayer",
                    "Microvision",
                    "PC Engine SuperGrafx",
                    "Turbografx-16/PC Engine CD",
                    "Dreamcast",
                    "Atari 8-bit",
                    "Vectrex",
                    "Donner Model 30",
                    "PDP-8",
                    "DEC GT40",
                    "Microcomputer",
                    "Ferranti Nimrod Computer",
                    "Apple IIGS",
                    "DOS",
                    "SwanCrystal",
                    "Fairchild Channel F",
                    "PC-8801",
                    "Virtual Boy",
                    "TRS-80",
                    "Nintendo Switch",
                    "Amazon Fire TV",
                    "VC 4000",
                    "1292 Advanced Programmable Video System",
                    "Tatung Einstein",
                    "Nintendo DSi",
                    "Neo Geo Pocket",
                    "Dragon 32/64",
                    "Amstrad PCW",
                    "Xbox",
                    "PDP-11",
                    "Virtual Console (Nintendo)",
                    "MSX2",
                    "Atari 7800",
                    "Sega CD",
                    "Game Boy Advance",
                    "Sega 32X",
                    "AY-3-8500",
                    "AY-3-8760",
                    "AY-3-8603",
                    "Nintendo 64",
                    "Neo Geo Pocket Color",
                    "Wii U",
                    "Sharp X1",
                    "Web browser",
                    "CDC Cyber 70",
                    "OnLive Game System",
                    "Acorn Archimedes",
                    "Amiga CD32",
                    "Philips CD-i",
                    "Sharp X68000",
                    "Nuon",
                    "Nintendo Entertainment System",
                    "AY-3-8610",
                    "Nintendo 3DS",
                    "Game Boy Color",
                    "Sega Master System/Mark III",
                    "Amiga",
                    "PlayStation Portable",
                    "TurboGrafx-16/PC Engine",
                    "Oculus VR",
                    "Playdia",
                    "PlayStation 3",
                    "Mac",
                    "Satellaview",
                    "Sega Saturn",
                    "Android",
                    "Commodore C64/128/MAX",
                    "Atari 5200",
                    "Intellivision",
                    "BlackBerry OS",
                    "Game & Watch",
                    "Imlac PDS-1",
                    "FM Towns",
                    "Nintendo PlayStation",
                    "NEC PC-6000 Series",
                    "FM-7",
                    "Nintendo DS",
                    "Atari ST/STE",
                    "PlayStation Vita",
                    "PlayStation 4",
                    "Atari Lynx",
                    "Commodore 16",
                    "Nintendo GameCube",
                    "N-Gage",
                    "Super Nintendo Entertainment System",
                    "Sharp MZ-2200",
                    "Super Famicom",
                    "Epoch Cassette Vision",
                    "Gear VR",
                    "PDP-10",
                    "Arcade",
                    "New Nintendo 3DS",
                    "Plug & Play",
                    "WonderSwan",
                    "Commodore VIC-20",
                    "Apple II",
                    "Windows Phone",
                    "Neo Geo AES",
                    "SG-1000",
                    "Windows Mixed Reality",
                    "Neo Geo MVS",
                    "Game Boy",
                    "Epoch Super Cassette Vision",
                    "Wii",
                    "Intellivision Amico",
                    "Google Stadia",
                    "PlayStation 5",
                    "Oculus Go",
                    "Oculus Rift",
                    "Bally Astrocade",
                    "Oculus Quest",
                    "BBC Microcomputer System",
                    "Legacy Mobile Device",
                    "Game.com",
                    "Linux",
                    "Ouya",
                    "PDP-1",
                    "TRS-80 Color Computer",
                    "Analogue electronics",
                    "Pokémon mini",
                    "EDSAC",
                    "HP 2100",
                    "Exidy Sorcerer",
                    "PDP-7",
                    "DVD Player",
                    "HP 3000",
                    "SDS Sigma 7",
                    "Daydream",
                    "Call-A-Computer time-shared mainframe computer system",
                    "Zeebo",
                    "PLATO",
                    "Blu-ray Player",
                    "ZX Spectrum",
                    "PC-FX",
                    "MSX",
                    "Evercade",
                    "OOParts",
                    "Sinclair ZX81",
                    "DUPLICATE Stadia",
                    "Casio Loopy",
                    "Xbox Series X|S",
                    "Atari 2600",
                    "Xbox 360",
                    "Xbox One",
                    "PlayStation",
                    "Family Computer",
                    "AirConsole",
                    "PlayStation VR2",
                    "Windows Mobile",
                    "Legacy Computer",
                    "Sinclair QL",
                    "Handheld Electronic LCD",
                    "Leapster Explorer/LeadPad Explorer",
                    "Nintendo 64DD",
                    "HyperScan",
                    "Palm OS",
                    "Mega Duck/Cougar Boy",
                    "Atari Jaguar CD",
                    "Watara/QuickShot Supervision",
                    "LeapTV",
                    "Leapster",
                    "Arduboy",
                    "V.Smile",
                    "Visual Memory Unit / Visual Memory System",
                    "PocketStation",
                    "Sega Mega Drive/Genesis",
                    "Meta Quest 2"};

            platforms[0] = getContext().getResources().getString(R.string.any_platform);


            List<String> years = new ArrayList<>();
            years.add(getContext().getResources().getString(R.string.any_year));
            for (int i = Calendar.getInstance().get(Calendar.YEAR); i >= 1958; i--) {
                years.add("" + i);
            }

            ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, genres);
            genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            genreSPN.setAdapter(genreAdapter);
            genreSPN.setSelection(lastSelectedGenre);

            ArrayAdapter<String> platformAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, platforms);
            platformAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            platformSPN.setAdapter(platformAdapter);
            platformSPN.setSelection(lastSelectedPlatform);

            ArrayAdapter<String> releaseYearAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, years);
            releaseYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            releaseYearSPN.setAdapter(releaseYearAdapter);
            releaseYearSPN.setSelection(lastSelectedReleaseYear);

            new MaterialAlertDialogBuilder(getContext())
                    .setView(customLayout)
                    .setTitle(R.string.search_filters_dialog_title)
                    .setPositiveButton(R.string.confirm_text, (dialogInterface, which) -> {

                        String genreInput, platformInput, releaseyearInput;

                        genreInput = genreSPN.getSelectedItem().toString();
                        platformInput = platformSPN.getSelectedItem().toString();
                        releaseyearInput = releaseYearSPN.getSelectedItem().toString();

                        lastSelectedGenre = genreAdapter.getPosition(genreInput);
                        lastSelectedPlatform = platformAdapter.getPosition(platformInput);
                        lastSelectedReleaseYear = releaseYearAdapter.getPosition(releaseyearInput);

                        if(isNetworkAvailable(getContext()) || !games.isEmpty()){

                            if (genreInput.equals(genres[0]) || platformInput.equals(platforms[0]) || releaseyearInput.equals(years.get(0))) {
                                games = new ArrayList<>(gamesCopy);

                                if(!sortingParameter.isEmpty()){

                                    //sorting decrescente
                                    Collections.sort(games, (o1, o2) -> {
                                        //non bello
                                        int result = 0;
                                        switch (sortingParameter) {
                                            case "Most popular":
                                                result = -Integer.compare(o1.getFollows(), o2.getFollows());
                                                break;

                                            case "Most recent":
                                                try {
                                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                                    Date date1 = formatter.parse(o1.getFirstReleaseDate());
                                                    Date date2 = formatter.parse(o2.getFirstReleaseDate());
                                                    result = -date1.compareTo(date2);
                                                } catch (ParseException e1) {
                                                    e1.printStackTrace();
                                                }
                                                break;

                                            case "Best rating":
                                                result = -Double.compare(o1.getRating(), o2.getRating());
                                                break;

                                            case "Alphabet":
                                                result = o1.getName().compareTo(o2.getName());
                                                break;

                                        }
                                        return result;
                                    });
                                }

                            }

                            if (!genreInput.equals(genres[0])) {
                                for (int i = games.size() - 1; i >= 0; i--) {
                                    boolean hasGenre = false;

                                    List<Genre> gameGenres = games.get(i).getGenres();

                                    if (gameGenres != null) {
                                        for (Genre genre : gameGenres) {
                                            if (genre.getName().equals(genreInput)) {
                                                hasGenre = true;
                                                break;
                                            }
                                        }

                                        if (!hasGenre) {
                                            games.remove(games.get(i));
                                        }
                                    }
                                }


                            }


                            if (!platformInput.equals(platforms[0])) {
                                for (int i = games.size() - 1; i >= 0; i--) {
                                    boolean hasPlatform = false;

                                    List<Platform> gamePlatforms = games.get(i).getPlatforms();

                                    if (gamePlatforms != null) {
                                        for (Platform platform : gamePlatforms) {
                                            if (platform.getName().equals(platformInput)) {
                                                hasPlatform = true;
                                                break;
                                            }
                                        }

                                        if (!hasPlatform) {
                                            games.remove(games.get(i));
                                        }
                                    }
                                }
                            }

                            if (!releaseyearInput.equals(years.get(0))) {
                                for (int i = games.size() - 1; i >= 0; i--) {
                                    String[] dateParts = games.get(i).getFirstReleaseDate().split("/");
                                    String yearOfRelease = dateParts[2];
                                    if (!yearOfRelease.equals(releaseyearInput))
                                        games.remove(games.get(i));
                                }
                            }

                            if(!firstLoad){
                                String text = String.format(getContext().getResources().getString(R.string.number_of_results), games.size(), userInput);
                                numberOfResults.setText(text);
                            }else{
                                numberOfResults.setText(R.string.explore_title);

                            }

                            showGamesOnRecyclerView(games);
                            adapter.notifyDataSetChanged();
                        }else{

                            Snackbar.make(view.findViewById(R.id.Coordinatorlyt), R.string.no_connection_message, Snackbar.LENGTH_LONG).show();
                        }

                    })
                    .setNegativeButton(R.string.cancel_text, (dialogInterface, i) -> {

                    })
                    .show();
        });
    }

    @Override
    public void onSuccess(List<GameApiResponse> gamesList, long lastUpdate,int count) {
        searchLoading.setVisibility(View.GONE);
        games = gamesList;
        gamesCopy = new ArrayList<>(games);

        if(!firstLoad){
            numberOfResults.setTextSize(15);
            numberOfResults.setTypeface(null, Typeface.NORMAL);
            String text = "";
            if(getContext() != null)
                 text = String.format(getContext().getResources().getString(R.string.number_of_results), games.size(), userInput);
            numberOfResults.setText(text);
        }

        showGamesOnRecyclerView(games);

        if(games.size() > 0){
            sorting.setVisibility(View.VISIBLE);
            filters.setVisibility(View.VISIBLE);
        }else{
            sorting.setVisibility(View.GONE);
            filters.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(String errorMessage) {
        Log.e("TAG", "query errata");
    }

    @Override
    public void onGameFavoriteStatusChanged(GameApiResponse game) {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(gameNameKey, userInput);
        outState.putString(sortingParameterKey, sortingParameter);
        outState.putInt(lastSelectedSortingParameterKey, lastSelectedSortingParameter);
        outState.putInt(lastSelectedGenreKey, lastSelectedGenre);
        outState.putInt(lastSelectedPlatformKey, lastSelectedPlatform);
        outState.putInt(lastSelectedReleaseYearKey, lastSelectedReleaseYear);
        outState.putString(resultNumberKey, numberOfResults.getText().toString());
        outState.putBoolean(firstLoadKey, firstLoad);


        //tutti i giochi
        outState.putParcelableArrayList(gamesKey, (ArrayList<? extends Parcelable>) games);
        outState.putParcelableArrayList(gamesCopyKey, (ArrayList<? extends Parcelable>) gamesCopy);

        Log.e("SEARCH FRAGMENT","ONSAVEINSTANCESTATE");

    }


    public void showGamesOnRecyclerView(List<GameApiResponse> gamesList) {
        // added data from arraylist to adapter class.
        adapter = new GameAdapter(gamesList, getContext());

        // setting grid layout manager to implement grid view.
        // in this method '2' represents number of columns to be displayed in grid view.
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);

        // at last set adapter to recycler view.
        gamesRV.setLayoutManager(layoutManager);
        gamesRV.setAdapter(adapter);
    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


}