package com.example.gamestorm.util.sort;

import com.example.gamestorm.model.GameApiResponse;

public class SortByMostPopular implements java.util.Comparator<GameApiResponse> {
    public int compare(GameApiResponse a, GameApiResponse b) {
        return -Integer.compare(a.getFollows(), b.getFollows());
    }
}
