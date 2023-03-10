package com.example.gamestorm.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class ReleaseDate {
    @SerializedName("y")
    int year;
    String date;
    ReleaseDate(){

    }
    public String getDate() {
        return date;
    }
    @NonNull
    @Override
    public String toString() {
        return "ReleaseDate{" +
                "date=" + date +
                '}';
    }
}
