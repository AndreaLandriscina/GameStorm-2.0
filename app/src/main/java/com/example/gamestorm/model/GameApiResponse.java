package com.example.gamestorm.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Entity
public class GameApiResponse {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    @TypeConverters(DataTypeConverter.class)
    private Cover cover;
    @TypeConverters(DataTypeConverter.class)
    private List<Genre> genres;
    @TypeConverters(DataTypeConverter.class)
    private List<Platform> platforms;
    private double rating;
    @SerializedName("total_rating")
    private double totalRating;
    @SerializedName("total_rating_count")
    private int totalRatingCount;
    @SerializedName("first_release_date")
    private int firstReleaseDate;
    private int follows;
    @SerializedName("franchises")
    @TypeConverters(DataTypeConverter.class)
    private List<Franchise> franchise;
    @SerializedName("involved_companies")
    @TypeConverters(DataTypeConverter.class)
    private List<InvolvedCompany> companies;
    @SerializedName("summary")
    private String description;

    @SerializedName("similar_games")
    @TypeConverters(DataTypeConverter.class)
    private List<Integer> similarGames;
    @TypeConverters(DataTypeConverter.class)
    private List<Screenshot> screenshots;
    @ColumnInfo(name = "is_popular")
    private boolean isPopular;
    @ColumnInfo(name = "is_best")
    private boolean isBest;
    @ColumnInfo(name = "is_latest")
    private boolean isLatest;
    @ColumnInfo(name = "is_incoming")
    private boolean isIncoming;
    @ColumnInfo(name = "is_wanted")
    private boolean isWanted;
    @ColumnInfo(name = "is_playing")
    private boolean isPlaying;
    @ColumnInfo(name = "is_played")
    private boolean isPlayed;

    @ColumnInfo(name = "is_explore")
    private boolean isExplore;
    @ColumnInfo(name = "is_synchronized")
    private boolean isSynchronized;

    @TypeConverters(DataTypeConverter.class)
    private List<Video> videos;

    private Long added;

    public List<Video> getVideos() {
        return videos;
    }
    public String getVideoId(){
        String id = null;
        for (Video video : videos){
            if (video.getName().equals("Trailer") || video.getName().equals("Gameplay video")) {
                id = video.getVideo_id();
            }
        }
        return id;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public Long getAdded() {
        return added;
    }

    public void setAdded(Long added) {
        this.added = added;
    }

    public GameApiResponse() {}

    public GameApiResponse(int id, String name, Cover cover, List<Genre> genres, List<Platform> platforms, List<ReleaseDate> releaseDates, double ratings, double totalRating, int totalRatingCount, int firstReleaseDate, int follows, List<Franchise> franchise, List<InvolvedCompany> companies, String description, List<Screenshot> screenshots, boolean isWanted,boolean isPlaying,boolean isPlayed, boolean isSynchronized ) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.genres = genres;
        this.platforms = platforms;
        this.rating = ratings;
        this.totalRating = totalRating;
        this.totalRatingCount = totalRatingCount;
        this.firstReleaseDate = firstReleaseDate;
        this.follows = follows;
        this.franchise = franchise;
        this.companies = companies;
        this.description = description;
        this.screenshots = screenshots;
        this.isWanted = isWanted;
        this.isPlaying = isPlaying;
        this.isPlayed = isPlayed;
        this.isSynchronized = isSynchronized;
    }

    public List<Integer> getSimilarGames() {
        return similarGames;
    }

    public void setSimilarGames(List<Integer> similarGames) {
        this.similarGames = similarGames;
    }

    public boolean isExplore() {
        return isExplore;
    }

    public void setExplore(boolean explore) {
        isExplore = explore;
    }

    public boolean isPopular() {
        return isPopular;
    }

    public void setPopular(boolean popular) {
        isPopular = popular;
    }

    public boolean isBest() {
        return isBest;
    }

    public void setBest(boolean best) {
        isBest = best;
    }

    public boolean isLatest() {
        return isLatest;
    }

    public void setLatest(boolean latest) {
        isLatest = latest;
    }

    public boolean isIncoming() {
        return isIncoming;
    }

    public void setIncoming(boolean incoming) {
        isIncoming = incoming;
    }

    public boolean isWanted() {
        return isWanted;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setWanted(boolean wanted) {
        isWanted = wanted;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }

    public boolean isSynchronized() {
        return isSynchronized;
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
    public void setGenres(List<Genre> genres){
        this.genres = genres;
    }

    public List<String> getGenresString() {
        List<String> genres = new ArrayList<>();
        if (this.genres != null){
            for (int i = 0; i < this.genres.size(); i++){
                genres.add(this.genres.get(i).name);
            }
        }
        return genres;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }
    public void setPlatforms(List<Platform> platforms){
        this.platforms = platforms;
    }

    public List<String> getPlatformsString() {
        List<String> platforms = null;
        if (this.platforms != null) {
            platforms = new ArrayList<>();
            for (int i = 0; i < this.platforms.size(); i++) {
                platforms.add(this.platforms.get(i).name);
            }
        }
        return platforms;
    }
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @NonNull
    @Override
    public String toString() {
        return "GameApiResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isPopular=" + isPopular +
                ", rating =" + totalRating +
                ", isBest=" + isBest +
                ", isLatest=" + isLatest +
                ", isIncoming=" + isIncoming +
                ", isWanted=" + isWanted +
                ", isPlaying=" + isPlaying +
                ", isPlayed=" + isPlayed +
                ", isExplore=" + isExplore +
                ", isSynchronized=" + isSynchronized +
                ", follows=" + follows +
                ", addes=" + added +
                ", screen=" + screenshots +
                ", video" + videos +
                '}';
    }

    public int getFirstReleaseDate(){
        return firstReleaseDate;
    }

    public void setFirstReleaseDate(int firstReleaseDate) {
        this.firstReleaseDate = firstReleaseDate;
    }

    public void setFranchise(List<Franchise> franchise) {
        this.franchise = franchise;
    }

    public String getFirstReleaseDateString() {
        Date date = new Date((long)firstReleaseDate*1000);
        // Display a date in day, month, year format
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }
    public void setfirstReleaseDateString(){

    }
    public void setFirstFranchise(){

    }
    public void setgenresString(){

    }
    public void setcompany(){

    }
    public void setplatformsString(){

    }
    public void setintReleaseDate(){

    }

    public int getIntReleaseDate(){
        return firstReleaseDate;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public List<Franchise> getFranchise(){
        return franchise;
    }
    public Franchise getFirstFranchise() {
        if (franchise != null)
            return franchise.get(0);
        return null;
    }

    public InvolvedCompany getInvolvedCompany() {
        if (companies != null)
            return companies.get(0);
        return null;
    }

    public List<InvolvedCompany> getCompanies() {
        return companies;
    }

    public void setCompanies(List<InvolvedCompany> companies) {
        this.companies = companies;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Screenshot> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<Screenshot> screenshots) {
        this.screenshots = screenshots;
    }

    public double getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(double totalRating) {
        this.totalRating = totalRating;
    }

    public int getTotalRatingCount() {
        return totalRatingCount;
    }

    public void setTotalRatingCount(int totalRatingCount) {
        this.totalRatingCount = totalRatingCount;
    }

    public void setSynchronized(boolean aSynchronized) {
        isSynchronized = aSynchronized;
    }
}

