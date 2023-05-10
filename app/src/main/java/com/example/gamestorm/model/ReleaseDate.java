package com.example.gamestorm.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class ReleaseDate {
    @SerializedName("y")
    int year;
    ReleaseDate(){

    }
    public int getYear() {
        return year;
    }
    @NonNull
    @Override
    public String toString() {
        return "ReleaseDate{" +
                "date=" + year +
                '}';
    }
}
