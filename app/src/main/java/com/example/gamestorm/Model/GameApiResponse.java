package com.example.gamestorm.Model;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class GameApiResponse {
    private int id;
    private String name;
    private Cover cover;
    private List<Genre> genres;
    private List<Platform> platforms;
    @SerializedName("release_dates")
    private List<ReleaseDate> releaseDates;
    private double ratings;

    public GameApiResponse() {}

    public GameApiResponse(int id, String name, Cover cover, List<Genre> genres, List<Platform> platforms, List<ReleaseDate> releaseDates, double ratings) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.genres = genres;
        this.platforms = platforms;
        this.releaseDates = releaseDates;
        this.ratings = ratings;
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

    public List<ReleaseDate> getReleaseDates() {
        return releaseDates;
    }

    public String getReleaseDate() {
        List<Calendar> datesLong = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        String dateString;
        for (ReleaseDate releaseDate : releaseDates) {
            dateString = releaseDate.getDate() + "000";
            Long dateLong = Long.parseLong(dateString);
            calendar.setTimeInMillis(dateLong);
            datesLong.add(calendar);
        }
        Calendar calendar1 = Collections.min(datesLong);
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        String[] months = dateFormatSymbols.getMonths();
        String month = months[calendar1.get(Calendar.MONTH)];
        String date = calendar1.get(Calendar.DAY_OF_MONTH)
                + " " + month
                + " " + calendar1.get(Calendar.YEAR);
        return date;
    }

    public void setReleaseDates(List<ReleaseDate> releaseDates) {
        this.releaseDates = releaseDates;
    }

    public double getRatings() {
        return ratings;
    }

    public void setRatings(double ratings) {
        this.ratings = ratings;
    }

    @Override
    public String toString() {
        return "GameApiResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cover=" + cover +
                ", genres=" + genres +
                ", platforms=" + platforms +
                ", releaseDates=" + releaseDates +
                ", rating=" + ratings +
                '}' + "\n";
    }
}

