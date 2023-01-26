package com.example.gamestorm.model;

import com.example.gamestorm.R;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GameApiResponse {
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GameApiResponse implements Parcelable {
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
    @SerializedName("release_dates")
    private List<ReleaseDate> releaseDates;
    private double rating;
    @SerializedName("first_release_date")
    private int firstReleaseDate;
    private int follows;


    public GameApiResponse() {}

    public GameApiResponse(int id, String name, Cover cover, List<Genre> genres, List<Platform> platforms, double ratings, int follows) {
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
        this.rating = ratings;
        this.follows = follows;
    }

    protected GameApiResponse(Parcel in) {
        id = in.readInt();
        name = in.readString();
        rating = in.readDouble();
        firstReleaseDate = in.readInt();
        follows = in.readInt();
    }

    public static final Creator<GameApiResponse> CREATOR = new Creator<GameApiResponse>() {
        @Override
        public GameApiResponse createFromParcel(Parcel in) {
            return new GameApiResponse(in);
        }

        @Override
        public GameApiResponse[] newArray(int size) {
            return new GameApiResponse[size];
        }
    };

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

<<<<<<< HEAD
    public String getReleaseDate() {
        if (releaseDate != null){
            Calendar calendar = Calendar.getInstance();
            long dateLong = Long.parseLong(releaseDate + "000");
            calendar.setTimeInMillis(dateLong);

            DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
            String[] months = dateFormatSymbols.getMonths();
            String month = months[calendar.get(Calendar.MONTH)];
            return calendar.get(Calendar.DAY_OF_MONTH)
                    + " " + month
                    + " " + calendar.get(Calendar.YEAR);
        } else {
            return null;
        }

    }

    public int getRatingCount() {
        return ratingCount;
=======
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
>>>>>>> Search
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

    public String getCompany() {
        if (companies != null)
            return companies.get(0).getCompany().getName();
        return null;
    }

    public List<InvolvedCompany> getCompanies() {
        return companies;
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
                ", releaseDates=" + releaseDates +
                ", rating=" + rating +
                ", firstReleaseDate=" + firstReleaseDate +
                ", follows=" + follows +
                '}' + "\n";
    }

    public String getFirstReleaseDate() {
        Date date = new Date((long)firstReleaseDate*1000);
        // Display a date in day, month, year format
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String stringFormattedDate = formatter.format(date);
        return stringFormattedDate;
    }


    public int getFollows() {
        return follows;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(rating);
        dest.writeInt(firstReleaseDate);
        dest.writeInt(follows);
    }
                ", releaseDate=" + releaseDate +
                ", ratingCount=" + ratingCount +
                ", rating=" + rating +
                ", description=" + description +
                ", franchise=" + franchise +
                ", screenshots=" + screenshots +
                ", companies=" + getCompanies() +
                '}' + "\n";
    }
}

