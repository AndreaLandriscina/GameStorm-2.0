package com.example.gamestorm.Model;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GameApiResponse {
    private int id;
    private String name;
    private Cover cover;
    private List<Genre> genres;
    private List<Platform> platforms;
    @SerializedName("first_release_date")
    private String releaseDate;
    private double ratings;
    @SerializedName("summary")
    private String description;
    @SerializedName("franchises")
    private List<Franchises> franchise;

    public GameApiResponse() {}

    public GameApiResponse(int id, String name, Cover cover, List<Genre> genres, List<Platform> platforms, String releaseDate, double ratings, String description, List<Franchises> franchise) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.genres = genres;
        this.platforms = platforms;
        this.releaseDate = releaseDate;
        this.ratings = ratings;
        this.description = description;
        this.franchise = franchise;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Cover getCover() {
        return cover;
    }

    public void setCover(Cover cover) {
        this.cover = cover;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public List<String> getGenresString() {
        List<String> genres = new ArrayList<>();
        for (int i = 0; i < this.genres.size(); i++){
            genres.add(this.genres.get(i).name);
        }
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public List<String> getPlatformsString() {
        List<String> platforms = new ArrayList<>();
        for (int i = 0; i < this.platforms.size(); i++){
            platforms.add(this.platforms.get(i).name);
        }
        return platforms;
    }

    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
    }

    public String getReleaseDate() {
        Calendar calendar = Calendar.getInstance();
        Long dateLong = Long.parseLong(releaseDate + "000");
        calendar.setTimeInMillis(dateLong);

        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        String[] months = dateFormatSymbols.getMonths();
        String month = months[calendar.get(Calendar.MONTH)];
        String date = calendar.get(Calendar.DAY_OF_MONTH)
                + " " + month
                + " " + calendar.get(Calendar.YEAR);
        return date;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getRatings() {
        return ratings;
    }

    public void setRatings(double ratings) {
        this.ratings = ratings;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Franchises getFranchise() {
        return franchise.get(0);
    }

    @Override
    public String toString() {
        return "GameApiResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cover=" + cover +
                ", genres=" + genres +
                ", platforms=" + platforms +
                ", releaseDate=" + releaseDate +
                ", rating=" + ratings +
                ", description=" + description +
                ", franchise=" + franchise +
                '}' + "\n";
    }
}

