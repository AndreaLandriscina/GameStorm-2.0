package com.example.gamestorm.model;

public class Genre {
    int id;
    String name;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "name='" + name + '\'' +
                '}';
    }
}
