package com.example.gamestorm.ui;

import android.app.AlertDialog;
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
import android.widget.ProgressBar;
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
    private List<GameApiResponse> gamesCopy;
    private EditText gameName;
    private Button search;
    private Button sorting;
    private Button filters;
    private TextView numberOfResults;
    private RecyclerView gamesRV;
    private ProgressBar searchLoading;
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
        searchLoading = view.findViewById(R.id.search_loading_PB);
        sortingParameter = "";
        lastSelectedSortingParameter = -1;
        lastSelectedGenre = 0;
        lastSelectedPlatform = 0;
        lastSelectedReleaseYear = 0;

        sorting.setVisibility(View.GONE);
        filters.setVisibility(View.GONE);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!games.isEmpty()){
                    games.clear();
                    adapter.notifyDataSetChanged();
                }

                lastSelectedSortingParameter = -1;

                lastSelectedGenre = 0;
                lastSelectedPlatform = 0;
                lastSelectedReleaseYear = 0;

                String user_input = gameName.getText().toString();
                //timestamp per ottenere solo giochi già usciti(su igdb si sono giochi che devono ancora uscire e che non hanno informazioni utili per l'utente)
                String query = "fields id, name, cover.url, follows, rating, first_release_date, genres.name, platforms.name; where first_release_date < " + System.currentTimeMillis() / 1000 + " & version_parent = null;search \"" + user_input + "\"; limit 500;";
                searchLoading.setVisibility(View.VISIBLE);
                iGamesRepository.fetchGames(query, 10000);
            }
        });

        sorting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        adapter.notifyDataSetChanged();
                        showGamesOnRecyclerView(games);
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


        filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AlertDialog builder instance to build the alert dialog
                AlertDialog.Builder filtersDialog = new AlertDialog.Builder(getContext());

                // title of the alert dialog
                filtersDialog.setTitle("Search Filters");

                // set the custom layout
                final View customLayout = getLayoutInflater().inflate(R.layout.dialog_filters, null);
                filtersDialog.setView(customLayout);


                Spinner genreSPN = customLayout.findViewById(R.id.genre_SPN);
                Spinner platformSPN = customLayout.findViewById(R.id.platform_SPN);
                Spinner releaseYearSPN = customLayout.findViewById(R.id.releaseyear_SPN);



                String[] genres = {"Any genre", "Fighting", "Shooter", "Music", "Platform", "Puzzle", "Racing", "Real Time Strategy (RTS)", "Role-playing (RPG)"
                        , "Simulator", "Sport", "Strategy", "Turn-based strategy (TBS)", "Tactical", "Quiz/Trivia",
                        "Hack and slash/Beat 'em up", "Pinball", "Adventure", "Arcade", "Visual Novel", "Indie",
                        "Card & Board Game", "MOBA", "Point-and-click"};

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

                List<String> years = new ArrayList<>();
                years.add("Any year");
                for (int i = Calendar.getInstance().get(Calendar.YEAR); i >= 1958; i--) {
                    years.add("" + i);
                }

                ArrayAdapter<String> genreAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, genres);
                genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                genreSPN.setAdapter(genreAdapter);
                genreSPN.setSelection(lastSelectedGenre);

                ArrayAdapter<String> platformAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, platforms);
                platformAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                platformSPN.setAdapter(platformAdapter);
                platformSPN.setSelection(lastSelectedPlatform);

                ArrayAdapter<String> releaseYearAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, years);
                releaseYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                releaseYearSPN.setAdapter(releaseYearAdapter);
                releaseYearSPN.setSelection(lastSelectedReleaseYear);

                filtersDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String genreInput, platformInput, releaseyearInput;

                        genreInput = genreSPN.getSelectedItem().toString();
                        platformInput = platformSPN.getSelectedItem().toString();
                        releaseyearInput = releaseYearSPN.getSelectedItem().toString();

                        lastSelectedGenre = genreAdapter.getPosition(genreInput);
                        lastSelectedPlatform = platformAdapter.getPosition(platformInput);
                        lastSelectedReleaseYear = releaseYearAdapter.getPosition(releaseyearInput);


                        if (genreInput.equals("Any genre") || platformInput.equals("Any platform") || releaseyearInput.equals("Any year")) {
                            games = new ArrayList<>(gamesCopy);

                            if(sortingParameter != ""){

                                Log.e("TAG", "dentro a sorting");

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

                            if (!genreInput.equals("Any genre")) {
                                for (int i = games.size() - 1; i >= 0; i--) {
                                    boolean hasGenre = false;

                                    List<Genre> gameGenres = games.get(i).getGenres();

                                    if (gameGenres != null) {
                                        for (Genre genre : gameGenres) {
                                            if (genre.getName().equals(genreInput)) {
                                                hasGenre = true;
                                            }
                                        }

                                        if (!hasGenre) {
                                            games.remove(games.get(i));
                                        }
                                    }
                                }


                            }


                            if (!platformInput.equals("Any platform")) {
                                for (int i = games.size() - 1; i >= 0; i--) {
                                    boolean hasPlatform = false;

                                    List<Platform> gamePlatforms = games.get(i).getPlatforms();

                                    if (gamePlatforms != null) {
                                        for (Platform platform : gamePlatforms) {
                                            if (platform.getName().equals(platformInput)) {
                                                hasPlatform = true;
                                            }
                                        }

                                        if (!hasPlatform) {
                                            games.remove(games.get(i));
                                        }
                                    }
                                }
                            }

                            if (!releaseyearInput.equals("Any year")) {
                                for (int i = games.size() - 1; i >= 0; i--) {
                                    String[] dateParts = games.get(i).getFirstReleaseDate().split("/");
                                    String yearOfRelease = dateParts[2];
                                    if (!yearOfRelease.equals(releaseyearInput))
                                        games.remove(games.get(i));
                                }
                            }



                        numberOfResults.setText(games.size() + " results found for " + "\"" + gameName.getText() + "\"");
                        showGamesOnRecyclerView(games);
                        adapter.notifyDataSetChanged();
                    }

                });

                filtersDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                // create and show the alert dialog
                AlertDialog dialog = filtersDialog.create();
                dialog.show();
            }
        });
    }

    @Override
    public void onSuccess(List<GameApiResponse> gamesList, long lastUpdate) {
        searchLoading.setVisibility(View.GONE);
        games = gamesList;
        gamesCopy = new ArrayList<>(games);
        numberOfResults.setText(games.size() + " results found for " + "\"" + gameName.getText() + "\"");
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
}