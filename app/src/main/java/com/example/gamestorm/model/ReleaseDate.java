package com.example.gamestorm.model;

import com.google.gson.annotations.SerializedName;

public class ReleaseDate {
    @SerializedName("y")
    int year;
    String date;
    public String getDate() {
        return date;
    }
    @Override
    public String toString() {
        return "ReleaseDate{" +
                "date=" + date +
                '}';
    }
}
