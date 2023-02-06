package com.example.gamestorm.model;

public class Screenshot {
    private String url;
    public Screenshot(String url) {
        this.url = url;
    }
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
