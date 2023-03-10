package com.example.gamestorm.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.gamestorm.repository.games.IGamesRepository;

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
