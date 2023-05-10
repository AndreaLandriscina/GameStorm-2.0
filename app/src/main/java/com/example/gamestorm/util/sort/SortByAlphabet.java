package com.example.gamestorm.util.sort;

import com.example.gamestorm.model.GameApiResponse;

public class SortByAlphabet implements java.util.Comparator<GameApiResponse> {
    public int compare(GameApiResponse a, GameApiResponse b) {
        return a.getName().compareTo(b.getName());
    }
}
