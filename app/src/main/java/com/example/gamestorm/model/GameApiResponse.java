package com.example.gamestorm.model;

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
    @SerializedName("total_rating")
    private double rating;
    @SerializedName("total_rating_count")
    private int ratingCount;
    @SerializedName("summary")
    private String description;
    @SerializedName("franchises")
    private List<Franchises> franchise;
    @SerializedName("involved_companies")
    private List<InvolvedCompany> companies;
    private List<Screenshot> screenshots;

    public GameApiResponse() {}

    public GameApiResponse(int id, String name, Cover cover, List<Genre> genres, List<Platform> platforms, String releaseDate, int ratingCount, double rating, String description, List<Franchises> franchise, List<InvolvedCompany> companies, List<Screenshot> screenshots) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.genres = genres;
        this.platforms = platforms;
        this.releaseDate = releaseDate;
        this.ratingCount = ratingCount;
        this.rating = rating;
        this.description = description;
        this.franchise = franchise;
        this.companies = companies;
        this.screenshots = screenshots;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Cover getCover() {
        return cover;
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

    public String getReleaseDate() {
        Calendar calendar = Calendar.getInstance();
        long dateLong = Long.parseLong(releaseDate + "000");
        calendar.setTimeInMillis(dateLong);

        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        String[] months = dateFormatSymbols.getMonths();
        String month = months[calendar.get(Calendar.MONTH)];
        return calendar.get(Calendar.DAY_OF_MONTH)
                + " " + month
                + " " + calendar.get(Calendar.YEAR);
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public double getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public Franchises getFranchise() {
        if (franchise != null)
            return franchise.get(0);
        return null;
    }

    public InvolvedCompany getCompanies() {
        if (companies != null)
            return companies.get(0);
        return null;
    }

    public List<Screenshot> getScreenshots() {
        return screenshots;
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
                ", ratingCount=" + ratingCount +
                ", rating=" + rating +
                ", description=" + description +
                ", franchise=" + franchise +
                ", screenshots=" + screenshots +
                '}' + "\n";
    }
}

