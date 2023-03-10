package com.example.gamestorm.ui;

import static com.example.gamestorm.util.Constants.LAST_UPDATE_EXPLORE;
import static com.example.gamestorm.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamestorm.adapter.RecyclerSearchAdapter;
import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.model.Genre;
import com.example.gamestorm.model.Platform;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.games.IGamesRepository;
import com.example.gamestorm.util.ServiceLocator;
import com.example.gamestorm.util.SharedPreferencesUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {

    private boolean exploreShowed;
    private List<GameApiResponse> games;
    private List<GameApiResponse> exploreCopy;
    private List<GameApiResponse> gamesCopy;
    private String userInput;
    private MaterialButton sorting;
    private MaterialButton filters;
    private TextView numberOfResults;
    private ProgressBar searchLoading;
    RecyclerSearchAdapter adapter;
    private String sortingParameter;
    private int lastSelectedSortingParameter;
    private int lastSelectedGenre;
    private int lastSelectedPlatform;
    private int lastSelectedReleaseYear;
    private GamesViewModel gamesViewModel;
    private ProgressBar progressBar;


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
        exploreShowed = true;
        games = new ArrayList<>();
        gamesCopy = new ArrayList<>();
        exploreCopy = new ArrayList<>();
        SearchView gameName = view.findViewById(R.id.game_name_SV);
        userInput = "";
        sorting = view.findViewById(R.id.sorting_B);
        filters = view.findViewById(R.id.filters_B);
        numberOfResults = view.findViewById(R.id.number_of_results_TV);
        sortingParameter = "";
        IGamesRepository iGamesRepository;
        try {
            iGamesRepository = ServiceLocator.getInstance().getGamesRepository(requireActivity().getApplication());
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        progressBar = requireView().findViewById(R.id.search_loading_PB);
        if (iGamesRepository != null) {
            progressBar.setVisibility(View.VISIBLE);
            gamesViewModel = new ViewModelProvider(this, new GamesViewModelFactory(iGamesRepository)).get(GamesViewModel.class);
        }

        RecyclerView gamesRV = view.findViewById(R.id.games_RV);
        adapter = new RecyclerSearchAdapter(games, getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        gamesRV.setLayoutManager(layoutManager);
        gamesRV.setAdapter(adapter);

        searchLoading = view.findViewById(R.id.search_loading_PB);
        String lastUpdate = "0";
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(requireActivity().getApplication());
        if (sharedPreferencesUtil.readStringData(
                SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE_EXPLORE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(
                    SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE_EXPLORE);
        }
        showExploreGames(lastUpdate);

        gameName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = requireActivity().getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(requireActivity());
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                exploreShowed = false;
                if(!games.isEmpty()){
                    games.clear();
                    gamesCopy.clear();
                    adapter.notifyDataSetChanged();
                }
                resetStatus();

                if(getContext()!= null && isNetworkAvailable(getContext())){
                    if (query.isEmpty()){
                        return false;
                    } else {
                        userInput = query;
                    }
                    //timestamp per ottenere solo giochi già usciti(su igdb si sono giochi che devono ancora uscire e che non hanno informazioni utili per l'utente)
                    searchLoading.setVisibility(View.VISIBLE);
                    numberOfResults.setText("");
                    gamesViewModel.getSearchedGames(query).observe(getViewLifecycleOwner(), gameApiResponses -> {
                        onSuccess(gameApiResponses, false);
                        games = gameApiResponses;
                        searchLoading.setVisibility(View.GONE);
                    });
                    return true;
                }else{
                    Toast.makeText(requireContext(), R.string.no_connection_message, Toast.LENGTH_LONG).show();
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty() && !exploreShowed){
                    resetStatus();
                }
                return false;
            }
        });

        sorting.setOnClickListener(v -> {

            final String[] listItems = requireContext().getResources().getStringArray(R.array.sorting_parameters);

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.sort_by_dialog_title)
                    .setSingleChoiceItems(listItems, lastSelectedSortingParameter, (dialog, i) -> {
                        sortingParameter = listItems[i];
                        lastSelectedSortingParameter = i;

                        if(isNetworkAvailable(requireContext()) || !games.isEmpty()){
                            sortGames(sortingParameter);


                        }else{
                            Toast.makeText(requireContext(), R.string.no_connection_message, Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();

                    })
                    .setNegativeButton(R.string.cancel_text, (dialogInterface, i) -> {

                    }).show();
        });


        filters.setOnClickListener(v -> {

            final View customLayout = getLayoutInflater().inflate(R.layout.dialog_filters, null);

            Spinner genreSPN = customLayout.findViewById(R.id.genre_SPN);
            Spinner platformSPN = customLayout.findViewById(R.id.platform_SPN);
            Spinner releaseYearSPN = customLayout.findViewById(R.id.releaseyear_SPN);

            String[] genres = requireContext().getResources().getStringArray(R.array.genres);
            genres[0] = requireContext().getResources().getString(R.string.any_genre);

            String[] platforms = requireContext().getResources().getStringArray(R.array.platforms);
            platforms[0] = requireContext().getResources().getString(R.string.any_platform);


            List<String> years = new ArrayList<>();
            years.add(requireContext().getResources().getString(R.string.any_year));
            for (int i = Calendar.getInstance().get(Calendar.YEAR); i >= 1958; i--) {
                years.add("" + i);
            }

            ArrayAdapter<String> genreAdapter = initializeSpinner(genreSPN, genres, lastSelectedGenre);
            ArrayAdapter<String> platformAdapter =initializeSpinner(platformSPN, platforms, lastSelectedPlatform);
            ArrayAdapter<String> releaseYearAdapter =initializeSpinner(releaseYearSPN, years.toArray(new String[0]), lastSelectedReleaseYear);

            new MaterialAlertDialogBuilder(requireContext())
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

                        if(isNetworkAvailable(requireContext()) || !games.isEmpty()){

                            if (genreInput.equals(genres[0]) || platformInput.equals(platforms[0]) || releaseyearInput.equals(years.get(0))) {
                                games = new ArrayList<>(gamesCopy);

                                if(!sortingParameter.isEmpty()){
                                    sortGames(sortingParameter);
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
                                    String[] dateParts = games.get(i).getFirstReleaseDateString().split("/");
                                    String yearOfRelease = dateParts[2];
                                    if (!yearOfRelease.equals(releaseyearInput))
                                        games.remove(games.get(i));
                                }
                            }

                            if(exploreShowed){
                                numberOfResults.setTextSize(30);
                                numberOfResults.setTypeface(null, Typeface.BOLD);
                                numberOfResults.setText(R.string.explore_title);
                            }else{
                                numberOfResults.setTextSize(15);
                                numberOfResults.setTypeface(null, Typeface.NORMAL);
                                String text = String.format(getContext().getResources().getString(R.string.number_of_results), games.size(), "Esplora");
                                numberOfResults.setText(text);
                            }
                            showGamesOnRecyclerView(games);
                        }else{
                            Toast.makeText(requireContext(), R.string.no_connection_message, Toast.LENGTH_LONG).show();
                        }

                    })
                    .setNegativeButton(R.string.cancel_text, (dialogInterface, i) -> {

                    })
                    .show();
        });
    }

    private void showExploreGames(String lastUpdate) {
        gamesViewModel.getExploreGames(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {

            progressBar.setVisibility(View.GONE);
            games = result;
            exploreCopy.addAll(result);
            onSuccess(result, true);
        });
    }


    public void onSuccess(List<GameApiResponse> gameApiResponses, boolean isExplore) {
        numberOfResults.setTextSize(30);
        numberOfResults.setTypeface(null, Typeface.BOLD);
        gamesCopy = new ArrayList<>(games);
        numberOfResults.setText(R.string.explore_title);

        if(isExplore){
            numberOfResults.setText(R.string.explore_title);
            numberOfResults.setTextSize(30);
            numberOfResults.setTypeface(null, Typeface.BOLD);
        }else{
            numberOfResults.setTextSize(15);
            numberOfResults.setTypeface(null, Typeface.NORMAL);
            String text;
            text = String.format(getContext().getResources().getString(R.string.number_of_results), gameApiResponses.size(), userInput);
            numberOfResults.setText(text);

        }



        if (gameApiResponses.size() > 0) {
            sorting.setVisibility(View.VISIBLE);
            filters.setVisibility(View.VISIBLE);
        } else {
            sorting.setVisibility(View.GONE);
            filters.setVisibility(View.GONE);
        }
        showGamesOnRecyclerView(gameApiResponses);
    }


    public void showGamesOnRecyclerView(List<GameApiResponse> gamesList) {
        adapter.setGames(gamesList);
        adapter.notifyDataSetChanged();
    }
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static class SortByMostPopular implements java.util.Comparator<GameApiResponse> {
        public int compare(GameApiResponse a, GameApiResponse b) {
            return -Integer.compare(a.getFollows(), b.getFollows());
        }
    }

    public static class SortByMostRecent implements java.util.Comparator<GameApiResponse> {
        public int compare(GameApiResponse a, GameApiResponse b) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date date1 = formatter.parse(a.getFirstReleaseDateString());
                Date date2 = formatter.parse(b.getFirstReleaseDateString());
                assert date1 != null;
                return -date1.compareTo(date2);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

            return -100;
        }
    }

    public static class SortByBestRating implements java.util.Comparator<GameApiResponse> {
        public int compare(GameApiResponse a, GameApiResponse b) {
            return -Double.compare(a.getTotalRating(), b.getTotalRating());
        }
    }

    public static class SortByAlphabet implements java.util.Comparator<GameApiResponse> {
        public int compare(GameApiResponse a, GameApiResponse b) {
            return a.getName().compareTo(b.getName());
        }
    }

    public void sortGames(String sortingParameter){
        switch (sortingParameter) {
            case "Most popular":
            case "Più popolare":
                Collections.sort(games, new SortByMostPopular());
                break;
            case "Most recent":
            case "Più recente":
                Collections.sort(games, new SortByMostRecent());
                break;
            case "Best rating":
            case "Voto migliore":
                Collections.sort(games, new SortByBestRating());
                break;
            case "Alphabet":
            case "Alfabeto":
                Collections.sort(games, new SortByAlphabet());
                break;
        }
        showGamesOnRecyclerView(games);
    }

    public void resetStatus(){
        sortingParameter = "";
        lastSelectedSortingParameter = -1;
        lastSelectedGenre = 0;
        lastSelectedPlatform = 0;
        lastSelectedReleaseYear = 0;
        numberOfResults.setText("");
        games.clear();
        gamesCopy.clear();
        showGamesOnRecyclerView(exploreCopy);
        adapter.notifyDataSetChanged();
    }

    public ArrayAdapter<String> initializeSpinner(Spinner spinner, String[] data, int lastSelectedItem){
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, data);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(lastSelectedItem);
        return spinnerAdapter;
    }
}