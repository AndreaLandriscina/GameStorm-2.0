package com.example.gamestorm.ui.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.gamestorm.repository.games.IGamesRepository;
import com.example.gamestorm.ui.viewModel.GamesViewModel;

public class GamesViewModelFactory implements ViewModelProvider.Factory {
    private final IGamesRepository iGamesRepository;

    public GamesViewModelFactory(IGamesRepository iGamesRepository) {
        this.iGamesRepository = iGamesRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new GamesViewModel(iGamesRepository);
    }

}
