package com.example.gamestorm.model;

import com.google.gson.annotations.SerializedName;
package com.example.gamestorm.model;

public class ReleaseDate {
    int id;
    @SerializedName("y")
    int year;
    String date;

    public String getDate() {

        return date;
    }

    @Override
    public String toString() {
        return "ReleaseDate{" +
                "date=" + date +
                '}';
    }
}
