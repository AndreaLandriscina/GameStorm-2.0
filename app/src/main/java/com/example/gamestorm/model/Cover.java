package com.example.gamestorm.model;

public class Cover {
    int id;
    String url;

    public Cover(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getUrl() {
        return "https:" + url;
    }

    @Override
    public String toString() {
        return "Cover{" +
                "url='" + url + '\'' +
                '}';
    }
}
