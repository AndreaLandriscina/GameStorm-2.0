package com.example.gamestorm.util.sort;

import com.example.gamestorm.model.GameApiResponse;

public class SortByBestRating implements java.util.Comparator<GameApiResponse> {
    public int compare(GameApiResponse a, GameApiResponse b) {
        return -Double.compare(a.getTotalRating(), b.getTotalRating());
    }
}
