package com.example.gamestorm.Model;

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
