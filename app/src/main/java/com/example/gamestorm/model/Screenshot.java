package com.example.gamestorm.model;

public class Screenshot {
    private String url;

    public String getUrl() {
        return "https:" + url;
    }

    @Override
    public String toString() {
        return "Screenshot{" +
                "url='" + url + '\'' +
                '}';
    }
}
