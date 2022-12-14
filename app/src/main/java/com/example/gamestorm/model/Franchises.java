package com.example.gamestorm.model;

public class Franchises {
    private final int id;
    private final String name;

    public Franchises(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Franchises{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
