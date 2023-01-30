package com.example.gamestorm.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserModel {
    private String id;
    private String name,email;
    public static ArrayList<Integer> desiredGames, playedGames, playingGames;

    public UserModel() {
    }

    public UserModel(String name, String email, String id) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.desiredGames=new ArrayList<>();
        this.playedGames=new ArrayList<>();
        this.playingGames=new ArrayList<>();
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
