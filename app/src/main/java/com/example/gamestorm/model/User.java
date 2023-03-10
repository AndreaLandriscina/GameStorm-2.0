package com.example.gamestorm.model;

import java.util.ArrayList;

public class User {
    private String id;
    private String name,email;
    public static ArrayList<Integer> desiredGames, playedGames, playingGames;

    public User() {
    }

    public User(String name, String email, String id) {
        this.name = name;
        this.email = email;
        this.id = id;
        desiredGames=new ArrayList<>();
        playedGames=new ArrayList<>();
        playingGames=new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Integer> getDesiredGames() {
        return desiredGames;
    }

    public ArrayList<Integer> getPlayedGames() {
        return playedGames;
    }

    public ArrayList<Integer> getPlayingGames() {
        return playingGames;
    }
}
