package com.example.gamestorm;

import java.net.MalformedURLException;
import java.net.URL;

public class GameData {
    private int id;
    private String name;
    private URL image;

    public GameData(int id, String name, URL image) throws MalformedURLException {
        this.setId(id);
        this.setName(name);
        this.setImage(image);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getImage() {
        return image;
    }

    public void setImage(URL image) {
        this.image = image;
    }
}
