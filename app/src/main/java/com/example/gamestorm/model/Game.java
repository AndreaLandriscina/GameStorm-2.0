package com.example.gamestorm.model;

import java.util.List;

public class Game {
    private int id;
    private String name;
    private Cover cover;
    private List<Genre> genres;
    private List<Platform> platforms;
    private List<ReleaseDate> releaseDates;
    private double rating;

    public Game(int id, String name, Cover cover, List<Genre> genres, List<Platform> platforms, List<ReleaseDate> releaseDates, double rating) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.genres = genres;
        this.platforms = platforms;
        this.releaseDates = releaseDates;
        this.rating = rating;
    }
}
