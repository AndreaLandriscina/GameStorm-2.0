package com.example.gamestorm.Model;

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
