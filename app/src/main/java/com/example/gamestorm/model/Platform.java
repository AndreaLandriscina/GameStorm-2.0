package com.example.gamestorm.model;

public class Platform {
    int id;
    String name;

    public String getName() {
        return name;
    }



    @Override
    public String toString() {
        return "Platform{" +
                "name='" + name + '\'' +
                '}';
    }
}
