package com.example.gamestorm.model;
public class User {
    private String id;
    private String name;
    private String email;
    private String photoProfile;


    public User() {
    }

    public User(String name, String email, String id, String photoProfile) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.photoProfile = photoProfile;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoProfile() {
        return photoProfile;
    }

    public void setPhotoProfile(String photoProfile) {
        this.photoProfile = photoProfile;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
