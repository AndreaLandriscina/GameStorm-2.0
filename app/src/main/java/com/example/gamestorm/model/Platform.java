package com.example.gamestorm.model;

public class Platform {
    String name;
    Platform(){

    }

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
