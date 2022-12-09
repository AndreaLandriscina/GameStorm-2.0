package com.example.gamestorm.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Game {

    private int id;

    private String name;

    private String company;

    private ArrayList<String> genres;
    @SerializedName("release_date")
    private String releaseDate;

    private ArrayList<String> platforms;

    private String cover;

    public Game(int id, String name, String company, ArrayList<String> genres, String releaseDate, ArrayList<String> platforms) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.genres = genres;
        this.releaseDate = releaseDate;
        this.platforms = platforms;
    }

    public Game(int id, String name, String cover) {
        this(id, name, cover, null, null, null);
    }

    public Game() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public String getRelease_date() {
        return releaseDate;
    }

    public void setRelease_date(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public ArrayList<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(ArrayList<String> platforms) {
        this.platforms = platforms;
    }
}
