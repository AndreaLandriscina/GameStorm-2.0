package com.example.gamestorm.model;

public class Company {
    private String name;
    private String description;
    public Company(String name, String description){
        this.name = name;
        this.description = description;
    }
    Company(){

    }
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
