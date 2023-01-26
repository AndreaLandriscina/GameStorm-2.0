package com.example.gamestorm.model;

public class ReleaseDate {
    int id;
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
