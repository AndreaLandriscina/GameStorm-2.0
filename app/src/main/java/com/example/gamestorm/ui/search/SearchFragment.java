package com.example.gamestorm.ui.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamestorm.adapter.RecyclerSearchAdapter;
import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.games.IGamesRepository;
import com.example.gamestorm.ui.viewModel.GamesViewModel;
import com.example.gamestorm.ui.viewModel.GamesViewModelFactory;
import com.example.gamestorm.util.ServiceLocator;
import com.example.gamestorm.util.sort.SortByAlphabet;
import com.example.gamestorm.util.sort.SortByBestRating;
import com.example.gamestorm.util.sort.SortByMostPopular;
import com.example.gamestorm.util.sort.SortByMostRecent;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class SearchFragment extends Fragment {
    private static boolean showSearch = false;
    private static boolean showExplore = true;
    private static boolean firstShowExplore = true;
    private List<GameApiResponse> games;
    private static List<GameApiResponse> exploreCopy = new ArrayList<>();
    private String userInput;
    private MaterialButton sorting;
    private MaterialButton filters;
    private ProgressBar searchLoading;
    RecyclerSearchAdapter adapter;
    private String sortingParameter;
    private int lastSelectedSortingParameter;
    private int lastSelectedGenre;
    private int lastSelectedPlatform;
    private int lastSelectedReleaseYear;
    private GamesViewModel gamesViewModel;
    private ProgressBar progressBar;
    private String yearInput;
    private RecyclerView recyclerView;
    private TextView noGameTextView;
    private TextInputEditText searchView;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        games = new ArrayList<>();
        searchView = view.findViewById(R.id.searchView);
        userInput = "";
        yearInput = null;
        sorting = view.findViewById(R.id.sorting_B);
        filters = view.findViewById(R.id.filters_B);
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

        recyclerView = view.findViewById(R.id.games_RV);
        adapter = new RecyclerSearchAdapter(games, getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        searchLoading = view.findViewById(R.id.search_loading_PB);
        if (showExplore) {
            showExploreGames();
        } else if (showSearch) {
            showSearchedGames(null);
        } else {
            showGamesOnRecyclerView(exploreCopy);
        }

        setSearchView();
        TextInputLayout searchViewLayout = requireView().findViewById(R.id.searchViewLayout);
        searchViewLayout.setEndIconOnClickListener(v -> {
            searchView.setText("");
            resetStatus();
        });
        setSorting();
        setFilters();
    }

    private void setSearchView() {
        searchView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = v.getText().toString();
                if (query.isEmpty()) {
                    resetStatus();
                    return false;
                }
                hideKeyboard();
                showExplore = false;
                if (!games.isEmpty()) {
                    games.clear();
                    adapter.notifyDataSetChanged();
                }

                if (getContext() != null && isNetworkAvailable(getContext())) {
                    userInput = query;
                    searchLoading.setVisibility(View.VISIBLE);
                    showSearch = true;
                    showSearchedGames(query);
                    return true;
                } else {
                    Toast.makeText(requireContext(), R.string.no_connection_message, Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            return false;
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = requireActivity().getCurrentFocus();
        if (view == null) {
            view = new View(requireActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showSearchedGames(String query) {
        gamesViewModel.getSearchedGames(query).observe(getViewLifecycleOwner(), gameApiResponses -> {
            games.clear();
            games.addAll(gameApiResponses);
            showExplore = false;
            searchLoading.setVisibility(View.GONE);
            showGamesOnRecyclerView(gameApiResponses);
        });
    }

    private void setFilters() {
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
            ArrayAdapter<String> platformAdapter = initializeSpinner(platformSPN, platforms, lastSelectedPlatform);
            ArrayAdapter<String> releaseYearAdapter = initializeSpinner(releaseYearSPN, years.toArray(new String[0]), lastSelectedReleaseYear);

            new MaterialAlertDialogBuilder(requireContext())
                    .setView(customLayout)
                    .setTitle(R.string.search_filters_dialog_title)
                    .setPositiveButton(R.string.confirm_text, (dialogInterface, which) -> {

                        String genreInput, platformInput;

                        genreInput = genreSPN.getSelectedItem().toString();
                        platformInput = platformSPN.getSelectedItem().toString();
                        yearInput = releaseYearSPN.getSelectedItem().toString();

                        lastSelectedGenre = genreAdapter.getPosition(genreInput);
                        lastSelectedPlatform = platformAdapter.getPosition(platformInput);
                        lastSelectedReleaseYear = releaseYearAdapter.getPosition(yearInput);
                        if (!genreInput.equals(genres[0]) || !platformInput.equals(platforms[0]) || !yearInput.equals(years.get(0))) {
                            recyclerView.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                        } else {
                            Log.i("reset", "reset");
                            resetStatus();
                            return;
                        }
                        if (!showSearch) {
                            if (genreInput.equals(genres[0]))
                                genreInput = null;
                            if (platformInput.equals(platforms[0]))
                                platformInput = null;
                            if (yearInput.equals(years.get(0)))
                                yearInput = null;
                            gamesViewModel.getSearchedGames(genreInput, platformInput, yearInput).observe(getViewLifecycleOwner(), gameApiResponses -> {
                                games = new ArrayList<>(gameApiResponses);
                                if (!sortingParameter.isEmpty()) {
                                    sortGames(sortingParameter);
                                }
                                showExplore = false;
                                showGamesOnRecyclerView(games);
                            });
                        } else {
                            List<GameApiResponse> gamesFiltered = new ArrayList<>();
                            boolean genreOk = false, platformOk = false, yearOk = false;
                            if (genreInput.equals(genres[0]))
                                genreOk = true;
                            if (platformInput.equals(platforms[0]))
                                platformOk = true;
                            if (yearInput.equals(years.get(0)))
                                yearOk = true;
                            for (GameApiResponse gameApiResponse : games) {
                                if (!genreOk) {
                                    if (gameApiResponse.getGenresString().contains(genreInput)) {
                                        genreOk = true;
                                    } else {
                                        continue;
                                    }
                                }
                                if (!platformOk) {
                                    if (gameApiResponse.getPlatformsString().contains(platformInput)) {
                                        platformOk = true;
                                    } else {
                                        continue;
                                    }
                                }
                                if (!yearOk) {
                                    if (gameApiResponse.getFirstYear().equals(yearInput)) {
                                        yearOk = true;
                                    } else {
                                        continue;
                                    }
                                }
                                gamesFiltered.add(gameApiResponse);
                            }
                            showGamesOnRecyclerView(gamesFiltered);
                        }
                    }).setNegativeButton("Reset", (dialogInterface, i) -> {
                        resetStatus();
                        dialogInterface.dismiss();
                    }).show();
        });
    }

    private void setSorting() {
        sorting.setOnClickListener(v -> {
            final String[] listItems = requireContext().getResources().getStringArray(R.array.sorting_parameters);
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.sort_by_dialog_title)
                    .setSingleChoiceItems(listItems, lastSelectedSortingParameter, (dialog, i) -> {
                        sortingParameter = listItems[i];
                        lastSelectedSortingParameter = i;
                        if (isNetworkAvailable(requireContext()) || !games.isEmpty()) {
                            sortGames(sortingParameter);
                            showGamesOnRecyclerView(games);
                        } else {
                            Toast.makeText(requireContext(), R.string.no_connection_message, Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }).setNegativeButton(R.string.cancel_text, (dialogInterface, i) -> dialogInterface.dismiss()).show();
        });
    }

    private void showExploreGames() {
        if (firstShowExplore) {
            gamesViewModel.getExploreGames(isNetworkAvailable(requireContext())).observe(getViewLifecycleOwner(), result -> {
                progressBar.setVisibility(View.GONE);
                Collections.shuffle(result);
                games = result;
                exploreCopy.addAll(games);
                firstShowExplore = false;
                showGamesOnRecyclerView(result);
            });
        } else {
            showGamesOnRecyclerView(exploreCopy);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void showGamesOnRecyclerView(List<GameApiResponse> gamesList) {
        Log.i("mostro", "mostro");
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        noGameTextView = requireView().findViewById(R.id.noResultText);
        if (!gamesList.isEmpty()) {
            sorting.setVisibility(View.VISIBLE);
            filters.setVisibility(View.VISIBLE);
            noGameTextView.setVisibility(View.GONE);
            List<GameApiResponse> temp = new ArrayList<>();
            if (yearInput != null) {
                for (GameApiResponse gameApiResponse : gamesList) {
                    if (gameApiResponse.getFirstYear().equals(yearInput)) {
                        temp.add(gameApiResponse);
                    }
                }
                adapter.setGames(temp);
            } else {
                adapter.setGames(gamesList);
            }
            adapter.notifyDataSetChanged();
        } else {
            noGameTextView.setVisibility(View.VISIBLE);
        }

    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void sortGames(String sortingParameter) {
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
    }

    public void resetStatus() {
        sortingParameter = "";
        lastSelectedSortingParameter = -1;
        lastSelectedGenre = 0;
        lastSelectedPlatform = 0;
        lastSelectedReleaseYear = 0;
        filters.setVisibility(View.VISIBLE);
        sorting.setVisibility(View.VISIBLE);
        games.clear();
        noGameTextView.setVisibility(View.GONE);
        hideKeyboard();
        showSearch = false;
        yearInput = null;
        showGamesOnRecyclerView(exploreCopy);
    }

    public ArrayAdapter<String> initializeSpinner(Spinner spinner, String[] data, int lastSelectedItem) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, data);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(lastSelectedItem);
        return spinnerAdapter;
    }
}